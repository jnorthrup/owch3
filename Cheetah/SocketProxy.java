package Cheetah;

import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import owch.Env;
import owch.Location;
import owch.MetaProperties;
import owch.Node;
import owch.PipeFactory;
import owch.PipeSocket;

/** SocketProxy */
public class SocketProxy extends Node implements Runnable {
    ServerSocket ss;

    public static void main(String[] args) {
        Map m = Env.parseCmdLine(args);
        if (!(m.containsKey("JMSReplyTo") &&
            m.containsKey("SourcePort") && m.containsKey("SourceHost") &&
            m.containsKey("ProxyPort"))) {
                System.out.println("\n\n******************** cmdline syntax error\n" +
                    "SocketProxy Agent usage:\n\n" +
                    "-name       (String)name\n" +
                    "-SourceHost (String)hostname/IP\n" +
                    "-SourcePort (int)port\n" +
                    "-ProxyPort  (int)port\n" +
                    "[-Clone 'host1[ ..hostn]']\n" +
                    "[-Deploy 'host1[ ..hostn]']\n" +
                    "$Id: SocketProxy.java,v 1.3 2001/09/23 10:20:10 grrrrr Exp $\n");
                System.exit(2);
        };
        SocketProxy d = new SocketProxy(m);
    };

  
    public int getSourcePort() {
        return Integer.decode((String)get("SourcePort")).intValue();
    }

    public int getProxyPort() {
        return Integer.decode((String)get("ProxyPort")).intValue();
    }

    /** ************************************************************
     *       this has the effect of taking over the
     * command of the http
     * service on the agent host and handling messages to marshal
     * http registrations
     ************************************************************ */
    public SocketProxy(Map m) {
        super(m);
        try {
            if (containsKey("Clone")) {
                String clist = (String)get("Clone");
                remove("Clone");
                Env.debug(500, getClass().getName() +
                    " **Cloning for " + clist);
                StringTokenizer st = new StringTokenizer(clist);
                while (st.hasMoreTokens()) {
                    clone_state1(st.nextToken());
                }
            };
            if (containsKey("Deploy")) {
                String clist = (String)get("Deploy");
                remove("Deploy");
                Env.debug(500, getClass().getName() +
                    " **Cloning for " + clist);
                StringTokenizer st = new StringTokenizer(clist);
                while (st.hasMoreTokens()) {
                    clone_state1(st.nextToken());
                }
                Thread.currentThread().sleep(15 * 1000); //kludge,
                // allow udp messages to arrive...
                System.exit(0); //TODO: allow our host to stay alive...
            };
            ss = new ServerSocket(getProxyPort());
            new Thread(this).start();
            spin();
        }
        catch (Exception e) {
            e.printStackTrace();
        };
    };

    public void spin() {
        Thread t = new Thread();
        try {
            t.start();
            while (!killFlag) {
                t.sleep(6000);
            } //todo: something
        }
        catch (Exception e) {
            e.printStackTrace();
        };
    }

    PipeFactory pf = new PipeFactory();

    public void run() {
        while (!killFlag) {
            try {
                //todo: time out somehow
                Socket inbound = ss.accept(); //wait for connection on ProxyPort
                PipeSocket ps = new PipeSocket(inbound);
                ps.connectTarget(
                    new Socket((String)get("SourceHost"),
                    getSourcePort()));
                ps.spin();
            }
            catch (InterruptedIOException e) {
                Env.debug(500, getClass().getName() +
                    "::interrupt " + e.getMessage());
            }
            catch (Exception e) {
                Env.debug(10, getClass().getName() + "::run " +
                    e.getMessage());
                e.printStackTrace();
            };
        };
    };

    public void clone_state1(String host) {
        MetaProperties n2 = new Location(this);
        n2.put("JMSType", "DeployNode");
        n2.put("Class", getClass().getName());
        n2.put("JMSReplyTo", getJMSReplyTo() + "." + host);
        //resource remains constant in this incarnation
        //n2.put( "Resource",get("Resource"));//produces 3 Strings
        n2.put("JMSDestination", host);
        send(n2);
    };
}
//$Log: SocketProxy.java,v $
//Revision 1.3  2001/09/23 10:20:10  grrrrr
//lessee
//
//2 major enhancements
//
//1) we now use reflection to decode message types.
//
//a message looks for handle_<JMSType> method that takes a MetaProperties as its input
//
//2) we now serve HTTP / 1.1 at every opportunity, sending content-length, and last-modified, and content type by default.  (WebPage still needs a few updates to catch up)
//
//Revision 1.1.2.1  2001/04/30 04:27:56  grrrrr
//SocketProxy + Deploy methods
//

