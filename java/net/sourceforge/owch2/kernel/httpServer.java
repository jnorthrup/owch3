package net.sourceforge.owch2.kernel;

import static net.sourceforge.owch2.kernel.ProtocolType.*;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.logging.*;

/**
 * Http server daemon used for sending files and routing agent notifications.
 *
 * @author James Northrup
 * @version $Id: httpServer.java,v 1.4 2005/06/04 02:26:24 grrrrr Exp $
 */
public class httpServer extends TCPServerWrapper implements ListenerReference, Runnable {
    int threads;
    private final static Map mimetypes = new HashMap();

    public ProtocolType getProtocol() {
        return Http;
    }

    public long getExpiration() {
        return (long) 0;
    }

    public int getThreads() {
        return threads;
    }

    public ServerWrapper getServer() {
        return this;
    }

    public void expire() {
        getServer().close();
    }

    public httpServer(InetAddress hostAddr, int port, int threads) throws IOException {
        super(port, hostAddr);
        this.threads = threads;
        try {
            for (int i = 0; i < threads; i++) {
                new Thread(this).start();
            }
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Logger.global.info("httpServer creation Failure");
        }
    }


    /**
     * called only on a new socket
     */
    public MetaProperties getRequest(Socket s) {
        String line = "";
        if (Env.getInstance().logDebug) Logger.global.info("httpServer.getRequest");
        Notification n = new Notification();
        try {
            DataInputStream ins = new DataInputStream(s.getInputStream());

            line = ins.readLine();
            n.load(ins);
            n.put("Request", line);
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Logger.global.info("had a DynServer Snag, retry");
        }
        if (Env.getInstance().logDebug) Logger.global.info("returning " + n.toString());
        return n;
    }

    /**
     * default action of an agent host is to just send a file.
     */
    public void sendFile(Socket s, String file) {
        /**
         * errors would send... HTTP/1.1 404 Not Found Date: Sun, 08 Apr 2001 21:31:24 GMT
         * Server: Apache/1.3.12 (Unix) mod_perl/1.24 Connection: close Content-Type: text/html; charset=iso-8859-1
         */
        try {
            boolean found = true;
            byte[] pref = null;
            if (file.startsWith("/")) {
                file = file.substring(1);
            }
            FileInputStream is = null;
            File fd = null;
            try {
                fd = new File(file);
                is = new FileInputStream(file);
            }
            catch (Exception e) {
                found = false;
                pref = new String("HTTP/1.1 404 " + e.getMessage() +
                        "\nConnection: close\n\n<!DOCTYPE HTML PUBLIC -//IETF//DTD HTML 2.0//EN><HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY><H1>" +
                        e.getMessage() + "</H1>The requested URL " + file +
                        " was not found on this server.<P></BODY></HTML>").getBytes();
            }
            if (pref == null) {
                FileInputStream i = (FileInputStream) is;
                String p = "HTTP/1.1 200 OK\n" + "Content-Type: " + getContentType(file) + "\n" + "Last-Modified: " +
                        new SimpleDateFormat().format(new Date(fd.lastModified())) + "\n" + "Content-Length: " +
                        fd.length() + "\n\n";
                pref = p.getBytes();
            }
            OutputStream os = new BufferedOutputStream(s.getOutputStream());
            os.write(pref, 0, pref.length);
            os.flush();
            if (found) {
                byte buf[] = new byte[ Math.min(32 * 1024, (int) fd.length()) ];
                int actual = 0;
                int avail = 0;
                while (true) {
                    avail = is.available();
                    if (avail > 0) {
                        actual = is.read(buf);
                    } else {
                        os.flush();
                        break;
                    }
                    os.write(buf, 0, actual);
                    if (Env.getInstance().logDebug)
                        Logger.global.info("httpd " + file + " sent " + actual + " bytes");
                }
            }
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug)
                Logger.global.info("httpd " + file + " connection exception " + e.getMessage());
        }
        finally {
            try {
                if (Env.getInstance().logDebug) Logger.global.info("httpd " + file + " connection closing");
                s.close();
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * this cuts the first line of the request into parts of the Request Notification so its easier to use
     */
    public void parseRequest(MetaProperties n) {
        String line = n.get("Request").toString();
        StringTokenizer st = new StringTokenizer(line);
        java.util.List list = new ArrayList();

        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        n.put("Method", list.get(0).toString().intern());
        n.put("Resource", list.get(1).toString());
        n.put("Protocol", list.get(2).toString());
    }

    /**
     * this is written to be over-ridden by the GateKeeper who looks at registered URL specs.  by default
     * it just sends a file it can find
     */
    public void dispatchRequest(Socket s, MetaProperties n) {
//        if (webRegistry == null) {
//            webRegistry = new httpRegistry();
//        }
//        return webRegistry;
        if (httpRegistry.getInstance().dispatchRequest(s, n) == false) {
            sendFile(s, n.get("Resource").toString());
        }
    }

    /**
     * sits and waits on a socket;
     */
    public void run() {
        while (!Env.getInstance().shutdown) {
            URL url = null;
            ArrayList list = new ArrayList();
            try {
                if (Env.getInstance().logDebug)
                    Logger.global.info("debug: " + Thread.currentThread().getName() + " init");
                Socket s = accept();
                MetaProperties n = getRequest(s);
                parseRequest(n);
                dispatchRequest(s, n);
            }
            catch (Exception e) {
                if (Env.getInstance().logDebug)
                    Logger.global.info("httpServer thread going down in flames on : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * used by the httpServer and anything else that wants mime types froma  file name.
     */
    static final public String getContentType(String resource) {
        int li = resource.lastIndexOf(".");
        if (li == -1) {
            return "application/octet-stream";
        }
        resource = resource.substring(li + 1, resource.length());
        resource = resource.toLowerCase().trim();
        resource = (String) mimetypes.get(resource);
        if (resource == null) {
            return "application/octet-stream";
        }
        return resource;
    }

    static {
        if (Env.getInstance().logDebug) Logger.global.info("... loading up the mime types.");
        httpServer.mimetypes.put("cpio", "application/x-cpio");
        httpServer.mimetypes.put("ai", "application/postscript");
        httpServer.mimetypes.put("eps", "application/postscript");
        httpServer.mimetypes.put("ps", "application/postscript");
        httpServer.mimetypes.put("aif", "audio/x-aiff");
        httpServer.mimetypes.put("aiff", "audio/x-aiff");
        httpServer.mimetypes.put("aifc", "audio/x-aiff");
        httpServer.mimetypes.put("asc", "text/plain");
        httpServer.mimetypes.put("txt", "text/plain");
        httpServer.mimetypes.put("au", "audio/basic");
        httpServer.mimetypes.put("snd", "audio/basic");
        httpServer.mimetypes.put("avi", "video/x-msvideo");
        httpServer.mimetypes.put("bcpio", "application/x-bcpio");
        httpServer.mimetypes.put("bin", "application/octet-stream");
        httpServer.mimetypes.put("dms", "application/octet-stream");
        httpServer.mimetypes.put("lha", "application/octet-stream");
        httpServer.mimetypes.put("lzh", "application/octet-stream");
        httpServer.mimetypes.put("exe", "application/octet-stream");
        httpServer.mimetypes.put("class", "application/octet-stream");
        httpServer.mimetypes.put("bmp", "image/bmp");
        httpServer.mimetypes.put("cpio", "application/x-cpio");
        httpServer.mimetypes.put("cpt", "application/mac-compactpro");
        httpServer.mimetypes.put("csh", "application/x-csh");
        httpServer.mimetypes.put("css", "text/css");
        httpServer.mimetypes.put("dcr", "application/x-director");
        httpServer.mimetypes.put("dir", "application/x-director");
        httpServer.mimetypes.put("dxr", "application/x-director");
        httpServer.mimetypes.put("doc", "application/msword");
        httpServer.mimetypes.put("dvi", "application/x-dvi");
        httpServer.mimetypes.put("etx", "text/x-setext");
        httpServer.mimetypes.put("ez", "application/andrew-inset");
        httpServer.mimetypes.put("gif", "image/gif");
        httpServer.mimetypes.put("gtar", "application/x-gtar");
        httpServer.mimetypes.put("hdf", "application/x-hdf");
        httpServer.mimetypes.put("hqx", "application/mac-binhex40");
        httpServer.mimetypes.put("html", "text/html");
        httpServer.mimetypes.put("htm", "text/html");
        httpServer.mimetypes.put("ice", "x-conference/x-cooltalk");
        httpServer.mimetypes.put("ief", "image/ief");
        httpServer.mimetypes.put("igs", "model/iges");
        httpServer.mimetypes.put("iges", "model/iges");
        httpServer.mimetypes.put("jpeg", "image/jpeg");
        httpServer.mimetypes.put("jpg", "image/jpeg");
        httpServer.mimetypes.put("jpe", "image/jpeg");
        httpServer.mimetypes.put("js", "application/x-javascript");
        httpServer.mimetypes.put("latex", "application/x-latex");
        httpServer.mimetypes.put("man", "application/x-troff-man");
        httpServer.mimetypes.put("me", "application/x-troff-me");
        httpServer.mimetypes.put("mid", "audio/midi");
        httpServer.mimetypes.put("midi", "audio/midi");
        httpServer.mimetypes.put("kar", "audio/midi");
        httpServer.mimetypes.put("mif", "application/vnd.mif");
        httpServer.mimetypes.put("movie", "video/x-sgi-movie");
        httpServer.mimetypes.put("mpeg", "video/mpeg");
        httpServer.mimetypes.put("mpg", "video/mpeg");
        httpServer.mimetypes.put("mpe", "video/mpeg");
        httpServer.mimetypes.put("mpga", "audio/mpeg");
        httpServer.mimetypes.put("mp2", "audio/mpeg");
        httpServer.mimetypes.put("mp3", "audio/mpeg");
        httpServer.mimetypes.put("ms", "application/x-troff-ms");
        httpServer.mimetypes.put("msh", "model/mesh");
        httpServer.mimetypes.put("mesh", "model/mesh");
        httpServer.mimetypes.put("silo", "model/mesh");
        httpServer.mimetypes.put("nc", "application/x-netcdf");
        httpServer.mimetypes.put("cdf", "application/x-netcdf");
        httpServer.mimetypes.put("oda", "application/oda");
        httpServer.mimetypes.put("pbm", "image/x-portable-bitmap");
        httpServer.mimetypes.put("pdb", "chemical/x-pdb");
        httpServer.mimetypes.put("xyz", "chemical/x-pdb");
        httpServer.mimetypes.put("pdf", "application/pdf");
        httpServer.mimetypes.put("pgm", "image/x-portable-graymap");
        httpServer.mimetypes.put("pgn", "application/x-chess-pgn");
        httpServer.mimetypes.put("png", "image/png");
        httpServer.mimetypes.put("pnm", "image/x-portable-anymap");
        httpServer.mimetypes.put("ppm", "image/x-portable-pixmap");
        httpServer.mimetypes.put("ppt", "application/vnd.ms-powerpoint");
        httpServer.mimetypes.put("qt", "video/quicktime");
        httpServer.mimetypes.put("mov", "video/quicktime");
        httpServer.mimetypes.put("ra", "audio/x-realaudio");
        httpServer.mimetypes.put("ram", "audio/x-pn-realaudio");
        httpServer.mimetypes.put("rm", "audio/x-pn-realaudio");
        httpServer.mimetypes.put("ras", "image/x-cmu-raster");
        httpServer.mimetypes.put("rgb", "image/x-rgb");
        httpServer.mimetypes.put("rpm", "audio/x-pn-realaudio-plugin");
        httpServer.mimetypes.put("rtf", "application/rtf");
        httpServer.mimetypes.put("rtf", "text/rtf");
        httpServer.mimetypes.put("rtx", "text/richtext");
        httpServer.mimetypes.put("sgml", "text/sgml");
        httpServer.mimetypes.put("sgm", "text/sgml");
        httpServer.mimetypes.put("sh", "application/x-sh");
        httpServer.mimetypes.put("shar", "application/x-shar");
        httpServer.mimetypes.put("site.zip", "application/x-stuffit");
        httpServer.mimetypes.put("skp", "application/x-koan");
        httpServer.mimetypes.put("skd", "application/x-koan");
        httpServer.mimetypes.put("skt", "application/x-koan");
        httpServer.mimetypes.put("skm", "application/x-koan");
        httpServer.mimetypes.put("smi", "application/smil");
        httpServer.mimetypes.put("smil", "application/smil");
        httpServer.mimetypes.put("spl", "application/x-futuresplash");
        httpServer.mimetypes.put("src/", "application/x-wais-source");
        httpServer.mimetypes.put("sv4cpio", "application/x-sv4cpio");
        httpServer.mimetypes.put("sv4crc", "application/x-sv4crc");
        httpServer.mimetypes.put("swf", "application/x-shockwave-flash");
        httpServer.mimetypes.put("t", "application/x-troff");
        httpServer.mimetypes.put("tr", "application/x-troff");
        httpServer.mimetypes.put("roff", "application/x-troff");
        httpServer.mimetypes.put("tar", "application/x-tar");
        httpServer.mimetypes.put("tcl", "application/x-tcl");
        httpServer.mimetypes.put("tex", "application/x-tex");
        httpServer.mimetypes.put("texinfo", "application/x-texinfo");
        httpServer.mimetypes.put("texi", "application/x-texinfo");
        httpServer.mimetypes.put("tiff", "image/tiff");
        httpServer.mimetypes.put("tif", "image/tiff");
        httpServer.mimetypes.put("tsv", "text/tab-separated-values");
        httpServer.mimetypes.put("ustar", "application/x-ustar");
        httpServer.mimetypes.put("vcd", "application/x-cdlink");
        httpServer.mimetypes.put("wav", "audio/x-wav");
        httpServer.mimetypes.put("wrl", "model/vrml");
        httpServer.mimetypes.put("vrml", "model/vrml");
        httpServer.mimetypes.put("xbm", "image/x-xbitmap");
        httpServer.mimetypes.put("xls", "application/vnd.ms-excel");
        httpServer.mimetypes.put("xml", "text/xml");
        httpServer.mimetypes.put("xpm", "image/x-xpixmap");
        httpServer.mimetypes.put("xwd", "image/x-xwindowdump");
        httpServer.mimetypes.put("zip", "application/zip");
    }

    ;
}