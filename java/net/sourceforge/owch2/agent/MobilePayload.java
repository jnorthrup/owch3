/**
 * MobilePayload.java
 *@author Jim Northrup
 */

package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * base class proof of concept for an object that can start up, suck
 * down some content, clone, and finally take over functions. this class carries content far away safe from harm.
 */
public class MobilePayload extends AbstractAgent implements Runnable {
    private Thread
            thread;

    public MobilePayload(Map<? extends Object, ? extends Object> m) {
        super(m);
        if (containsKey("Source")) {
            init((String) get("JMSReplyTo"), (String) get("Source"), (String) get("Resource"));
        } else {
            init((String) get("JMSReplyTo"), (String) get("Resource"));
        }
    }

    public MobilePayload(String name, String file) {
        super();
        init(name, file);
    }

    public MobilePayload(String name, String url, String resource) {
        put("JMSReplyTo", name);
        put("Resource", resource);
        remove("Source");
        init(name, url, resource);
    }

    public static void main(String[] args) {
        Map<? extends Object, ? extends Object> m = Env.getInstance().parseCommandLineArgs(args);
        if (!(m.containsKey("JMSReplyTo") && m.containsKey("Resource"))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" + "MobilePayload Agent usage:\n\n" + "-name name\n" +
                    "-Resource 'resource' -- the resource starting with '/' that is registered on the GateKeeper\n" +
                    "-Source 'file' -- the file \n" + "[-Content-Type 'application/msword']\n" + "[-Clone 'host1[ ..hostn]']\n" +
                    "[-Deploy 'host1[ ..hostn]']\n" + "$Id: MobilePayload.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $\n");
        }
        ;
        new MobilePayload(m);
    }

    ;

    /**
     * this is a set of headers that is simply nice to have...
     */
    protected Message nice = new Message();

    /**
     * this is a set of header fields that is simply nice to have...
     */
    protected final String[] nice_headers =
            {
                    "Last-Modified",
                    "Content-Type",
                    "Content-Encoding"
            };

    /**
     * interval defines our random interval of re-registration starting with 1/2n..n milliseconds
     */
    private long interval = 60 * 1000 * 2;
    protected byte[] payload; //for now we'll just assume payload is a static buffer
    //todo: servelet api

    /**
     * our thread function is to periodically wake up and notify both the Domain and the Gatekeeper of our existance every
     * so often. this allows changing of the guard and a roundabout load balancing of competing MobilePayload clones
     * sharing a single gatekeeper
     */
    public void run() {
        while (!killFlag) {
            sendRegistrations();
            relocate();

            long tim = (long) (Math.random() * (interval / 2.0) + (interval / 2.0));
            if (false)
                Logger.getAnonymousLogger().info(getClass().getName() + " waiting for " + tim + " ms.");
            try {
                Thread.currentThread().sleep(tim);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }


        }
    }

    /**
     * this tells our (potentially clone) web page to stop re-registering.  it will cease to spin.
     */
    public void handle_Dissolve(MetaProperties n) {
        thread.interrupt();
        Message n2 = new Message();
        n2.put("JMSDestination", "GateKeeper");
        n2.put("JMSType", "UnRegister");
        n2.put("URLSpec", get("Resource").toString());
        send(n2);
        super.handle_Dissolve(n);
    }

    ;

/*
*  MobilePayload Constructor
*
*  Initializes communication
*
*  params:  a map -  lots o junk inside..
*/

/*
*  MobilePayload Constructor
*
*  Initializes communication
*
*  params: name -- we name our agent anything we want..
*
*  url -- name of a file
*/

    public void init(String name, String file) {
        put("JMSReplyTo", name);
        put("Resource", file);
        inductFile(file);
    }

/*
*  MobilePayload Constructor
*
*  Initializes communication
*
*  params: name -- we name our agent anything we want..
*
*  url -- name of a file
*/

    public void init(String name, String url, String resource) {
        inductURL(url);
    }

    public void inductURL(String url) {
        try {
            URL u = new URL(url);
            URLConnection uc = u.openConnection();
            for (int i = 0; i < nice_headers.length; i++) {
                String hdr = uc.getHeaderField(nice_headers[i]);
                if (hdr != null) {
                    nice.put(nice_headers[i], get(nice_headers[i]));
                }
            }
            InputStream is = uc.getInputStream();
            inductStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ;

    public void inductFile(String file) {
        try {
            String resource = file;
            if (resource.startsWith("/")) {
                resource = resource.substring(1);
            }
//the assumption is being made that our current work directory
//wont change...
            put("Resource", file);
            InputStream is;
            is = new URL(file).openConnection().getInputStream();
            inductStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inductStream(InputStream is) {
        ProtocolType.ipc.routerInstance().pathExists(this);
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[16384];
            int actual = 0;
            int avail = 0;
            while (actual != -1) {
                avail = is.available();
                actual = is.read(buf);
                if (actual >= 0) {
                    os.write(buf, 0, actual);
                    if (false)
                        Logger.getAnonymousLogger().info(getClass().getName() + ":" + get("Resource").toString() +
                                " slurped up " + actual + " bytes");
                }
            }
            payload = os.toByteArray();
            os.flush();
            os.close();
            thread = new Thread(this, getClass().getName() + ":" + get("JMSReplyTo()") + ":" + get("Resource"));
            thread.start();
            if (false)
                Logger.getAnonymousLogger().info("-=-=-=-=-" + getClass().getName() + ":" + get("Resource").toString() + " ThreadStart ");
        } catch (Exception e) {
            if (false)
                Logger.getAnonymousLogger().info(getClass().getName() + ":" + get("Resource").toString() + " failure " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    /**
     * sets the IM HERE interval for our periodic re-registration tasks.
     */
    public void setInterval(long ival) {
        Thread.yield();
        interval = ival;
    }

    public void sendRegistrations() {
        linkTo(null);
        String resource = get("Resource").toString();

        MetaProperties localHttpLocation = ProtocolType.Http.getLocation();
        Location location = new Location(localHttpLocation);
        location.put("JMSReplyTo", getJMSReplyTo());

        Env.getInstance().getHttpRegistry().registerItem(resource, location);
        Message n2 = new Message();
        n2.put("JMSDestination", "GateKeeper");
        n2.put("JMSType", "Register");
        n2.put("URLSpec", resource);
        n2.put("URLFwd", location.getURI());
        send(n2);
    }

    /**
     * sendPayload(socket) dumps our byte[] to the socket client..
     *
     * @param socket outbound socket
     */
    public void sendPayload(Socket socket) {
        OutputStream os = null;
        try {
            if (!nice.containsKey("Content-Type")) {
                nice.put("Content-Type", "application/octet-stream");
            }
            if (!nice.containsKey("Content-Length") && !"text/html".intern().equals(nice.get("Content-Type"))) {
                nice.put("Content-Length", "" + payload.length);
            }
            os = socket.getOutputStream();

            os.write("HTTP/1.1 200 OK\n".getBytes());
            for (String nice_header : nice_headers) {
                if (containsKey(nice_header)) {
                    nice.put(nice_header, get(nice_header));
                }

            }
            nice.save(os);

            int actual = 0;
            ByteArrayInputStream is = new ByteArrayInputStream(payload);
            byte[] buf = new byte[16 * 1024];
            do {

                while (0 < is.available()) {
                    actual = is.read(buf);
                    if (0 < actual) {
                        os.write(buf, 0, actual);
                    }
                }
            } while (actual != -1);
            os.flush();
            os.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handle_httpd(MetaProperties n) {
        Socket s = Env.getInstance().getHttpRegistry().httpdSockets.remove("_Socket");
        sendPayload(s);
    }

    /**
     * this message tells us to write the payload and where to write it
     *
     * @param message
     */
    public void handle_WriteFile(MetaProperties message) {
        String path;
        path = (String) message.get("Path");
        String filename;
        filename = (String) message.get("Filename");


        File file = new File(path, filename);

        boolean exists = file.exists();
        String mOverwrite = (String) message.get("OverWrite");
        boolean overwrite = "true".equals(mOverwrite);

        if (!overwrite && exists) try {
            throw new Exception("Overwrite not specified");
        } catch (Exception e) {
            Logger.getAnonymousLogger().severe(Arrays.toString(e.getStackTrace()));  //TODO: clarify autogen exception
            return;
        }

        File tmpfile;
        tmpfile = new File(path, filename + "...");

        int blocksize;
        blocksize = 32 * 1024;
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(tmpfile), blocksize);
            out.write(payload);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }

}
