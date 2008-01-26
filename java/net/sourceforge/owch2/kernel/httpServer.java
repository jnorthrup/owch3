package net.sourceforge.owch2.kernel;

import net.sourceforge.owch2.agent.*;
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
    private final static Map<String, String> mimetypes = new HashMap<String, String>();

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
            if (false) Logger.getAnonymousLogger().info("httpServer creation Failure");
        }
    }


    /**
     * called only on a new socket
     */
    public MetaProperties getRequest(Socket s) {
        String line = "";
        if (false) Logger.getAnonymousLogger().info("httpServer.getRequest");
        Message n = new Message();
        try {
            DataInputStream ins = new DataInputStream(s.getInputStream());

            line = ins.readLine();
            n.load(ins);
            n.put("Request", line);
        }
        catch (Exception e) {
            if (false) Logger.getAnonymousLogger().info("had a DynServer Snag, retry");
        }
        if (false) Logger.getAnonymousLogger().info("returning " + n.toString());
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
                pref = MessageFormat.format("HTTP/1.1 404 {0}\nConnection: close\n\n<!DOCTYPE HTML PUBLIC -//IETF//DTD HTML 2.0//EN><HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY><H1>{1}</H1>The requested URL {2} was not found on this server.<P></BODY></HTML>", e.getMessage(), e.getMessage(), file).getBytes();
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
                byte buf[] = new byte[Math.min(32 * 1024, (int) fd.length())];
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
                    if (false)
                        Logger.getAnonymousLogger().info("httpd " + file + " sent " + actual + " bytes");
                }
            }
        }
        catch (Exception e) {
            if (false)
                Logger.getAnonymousLogger().info("httpd " + file + " connection exception " + e.getMessage());
        }
        finally {
            try {
                if (false) Logger.getAnonymousLogger().info("httpd " + file + " connection closing");
                s.close();
            }
            catch (Exception e) {
            }
        }
    }

    /**
     * this cuts the first line of the request into parts of the Request Message so its easier to use
     */
    public void parseRequest(MetaProperties n) {
        String line = n.get("Request").toString();
        StringTokenizer st = new StringTokenizer(line);
        List<String> list = new ArrayList<String>();

        while (st.hasMoreTokens()) {
            list.add(st.nextToken());
        }
        n.put("Method", list.get(0).toString().intern());
        n.put(GateKeeper.RESOURCE_KEY, list.get(1).toString());
        n.put("Protocol", list.get(2).toString());
    }

    /**
     * this is written to be over-ridden by the GateKeeper who looks at registered URL specs.  by default
     * it just sends a file it can find
     */
    public void dispatchRequest(Socket s, MetaProperties n) {

        if (!Env.getInstance().getHttpRegistry().dispatchRequest(s, n)) {
            sendFile(s, (String) n.get(GateKeeper.RESOURCE_KEY));
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
                if (false)
                    Logger.getAnonymousLogger().info("debug: " + Thread.currentThread().getName() + " init");
                Socket s = accept();
                MetaProperties n = getRequest(s);
                parseRequest(n);
                dispatchRequest(s, n);
            }
            catch (Exception e) {
                if (false)
                    Logger.getAnonymousLogger().info("httpServer thread going down in flames on : " + e.getMessage());
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
        resource = mimetypes.get(resource);
        if (resource == null) {
            return "application/octet-stream";
        }
        return resource;
    }

    static {
        mimetypes.put("cpio", "application/x-cpio");
        mimetypes.put("ai", "application/postscript");
        mimetypes.put("eps", "application/postscript");
        mimetypes.put("ps", "application/postscript");
        mimetypes.put("aif", "audio/x-aiff");
        mimetypes.put("aiff", "audio/x-aiff");
        mimetypes.put("aifc", "audio/x-aiff");
        mimetypes.put("asc", "text/plain");
        mimetypes.put("txt", "text/plain");
        mimetypes.put("au", "audio/basic");
        mimetypes.put("snd", "audio/basic");
        mimetypes.put("avi", "video/x-msvideo");
        mimetypes.put("bcpio", "application/x-bcpio");
        mimetypes.put("bin", "application/octet-stream");
        mimetypes.put("dms", "application/octet-stream");
        mimetypes.put("lha", "application/octet-stream");
        mimetypes.put("lzh", "application/octet-stream");
        mimetypes.put("exe", "application/octet-stream");
        mimetypes.put("class", "application/octet-stream");
        mimetypes.put("bmp", "image/bmp");
        mimetypes.put("cpio", "application/x-cpio");
        mimetypes.put("cpt", "application/mac-compactpro");
        mimetypes.put("csh", "application/x-csh");
        mimetypes.put("css", "text/css");
        mimetypes.put("dcr", "application/x-director");
        mimetypes.put("dir", "application/x-director");
        mimetypes.put("dxr", "application/x-director");
        mimetypes.put("doc", "application/msword");
        mimetypes.put("dvi", "application/x-dvi");
        mimetypes.put("etx", "text/x-setext");
        mimetypes.put("ez", "application/andrew-inset");
        mimetypes.put("gif", "image/gif");
        mimetypes.put("gtar", "application/x-gtar");
        mimetypes.put("hdf", "application/x-hdf");
        mimetypes.put("hqx", "application/mac-binhex40");
        mimetypes.put("html", "text/html");
        mimetypes.put("htm", "text/html");
        mimetypes.put("ice", "x-conference/x-cooltalk");
        mimetypes.put("ief", "image/ief");
        mimetypes.put("igs", "model/iges");
        mimetypes.put("iges", "model/iges");
        mimetypes.put("jpeg", "image/jpeg");
        mimetypes.put("jpg", "image/jpeg");
        mimetypes.put("jpe", "image/jpeg");
        mimetypes.put("js", "application/x-javascript");
        mimetypes.put("latex", "application/x-latex");
        mimetypes.put("man", "application/x-troff-man");
        mimetypes.put("me", "application/x-troff-me");
        mimetypes.put("mid", "audio/midi");
        mimetypes.put("midi", "audio/midi");
        mimetypes.put("kar", "audio/midi");
        mimetypes.put("mif", "application/vnd.mif");
        mimetypes.put("movie", "video/x-sgi-movie");
        mimetypes.put("mpeg", "video/mpeg");
        mimetypes.put("mpg", "video/mpeg");
        mimetypes.put("mpe", "video/mpeg");
        mimetypes.put("mpga", "audio/mpeg");
        mimetypes.put("mp2", "audio/mpeg");
        mimetypes.put("mp3", "audio/mpeg");
        mimetypes.put("ms", "application/x-troff-ms");
        mimetypes.put("msh", "model/mesh");
        mimetypes.put("mesh", "model/mesh");
        mimetypes.put("silo", "model/mesh");
        mimetypes.put("nc", "application/x-netcdf");
        mimetypes.put("cdf", "application/x-netcdf");
        mimetypes.put("oda", "application/oda");
        mimetypes.put("pbm", "image/x-portable-bitmap");
        mimetypes.put("pdb", "chemical/x-pdb");
        mimetypes.put("xyz", "chemical/x-pdb");
        mimetypes.put("pdf", "application/pdf");
        mimetypes.put("pgm", "image/x-portable-graymap");
        mimetypes.put("pgn", "application/x-chess-pgn");
        mimetypes.put("png", "image/png");
        mimetypes.put("pnm", "image/x-portable-anymap");
        mimetypes.put("ppm", "image/x-portable-pixmap");
        mimetypes.put("ppt", "application/vnd.ms-powerpoint");
        mimetypes.put("qt", "video/quicktime");
        mimetypes.put("mov", "video/quicktime");
        mimetypes.put("ra", "audio/x-realaudio");
        mimetypes.put("ram", "audio/x-pn-realaudio");
        mimetypes.put("rm", "audio/x-pn-realaudio");
        mimetypes.put("ras", "image/x-cmu-raster");
        mimetypes.put("rgb", "image/x-rgb");
        mimetypes.put("rpm", "audio/x-pn-realaudio-plugin");
        mimetypes.put("rtf", "application/rtf");
        mimetypes.put("rtf", "text/rtf");
        mimetypes.put("rtx", "text/richtext");
        mimetypes.put("sgml", "text/sgml");
        mimetypes.put("sgm", "text/sgml");
        mimetypes.put("sh", "application/x-sh");
        mimetypes.put("shar", "application/x-shar");
        mimetypes.put("site.zip", "application/x-stuffit");
        mimetypes.put("skp", "application/x-koan");
        mimetypes.put("skd", "application/x-koan");
        mimetypes.put("skt", "application/x-koan");
        mimetypes.put("skm", "application/x-koan");
        mimetypes.put("smi", "application/smil");
        mimetypes.put("smil", "application/smil");
        mimetypes.put("spl", "application/x-futuresplash");
        mimetypes.put("src/", "application/x-wais-source");
        mimetypes.put("sv4cpio", "application/x-sv4cpio");
        mimetypes.put("sv4crc", "application/x-sv4crc");
        mimetypes.put("swf", "application/x-shockwave-flash");
        mimetypes.put("t", "application/x-troff");
        mimetypes.put("tr", "application/x-troff");
        mimetypes.put("roff", "application/x-troff");
        mimetypes.put("tar", "application/x-tar");
        mimetypes.put("tcl", "application/x-tcl");
        mimetypes.put("tex", "application/x-tex");
        mimetypes.put("texinfo", "application/x-texinfo");
        mimetypes.put("texi", "application/x-texinfo");
        mimetypes.put("tiff", "image/tiff");
        mimetypes.put("tif", "image/tiff");
        mimetypes.put("tsv", "text/tab-separated-values");
        mimetypes.put("ustar", "application/x-ustar");
        mimetypes.put("vcd", "application/x-cdlink");
        mimetypes.put("wav", "audio/x-wav");
        mimetypes.put("wrl", "model/vrml");
        mimetypes.put("vrml", "model/vrml");
        mimetypes.put("xbm", "image/x-xbitmap");
        mimetypes.put("xls", "application/vnd.ms-excel");
        mimetypes.put("xml", "text/xml");
        mimetypes.put("xpm", "image/x-xpixmap");
        mimetypes.put("xwd", "image/x-xwindowdump");
        mimetypes.put("zip", "application/zip");
    }

    ;
}