package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/** SocketProxy

connects incoming sockets to outbound host,port destinations.

note:
hosts and ports can be specified 1:1, 1:n, n:1 or n:n.

lists of ports and hosts are tried in order specified.
 @version $Id: SocketProxy.java,v 1.1 2002/12/08 16:05:49 grrrrr Exp $
 */
public class SocketProxy extends AbstractAgent implements Runnable {
    private Collection srcPort = new ArrayList(),srcHost = new ArrayList();
    private Iterator srcPort_i,srcHost_i;
    String AgentPort;
    private ServerSocket ss;
    private PipeFactory pf = new PipeFactory();

    public static void main(String[] args) {
        Map m = Env.parseCommandLineArgs(args);
        final String[] ka = {"JMSReplyTo", "SourcePort", "SourceHost", "AgentPort"};

        if (!m.keySet().containsAll(Arrays.asList(ka))) {
            Env.cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                            "SocketProxy Agent usage:\n\n" +
                    "-name       (String)name\n" +
                    "-SourceHost (String)'hostname/IP[ ...n]'\n" +
                    "-SourcePort (int)'port[ ...n]'\n" +
                    "-AgentPort  (int)port\n" +
                    "[-Buffer (int)128+]\n" +
                    "[-{Inflate|Deflate} (String){agent|source|both} ..n]\n" +
                    "[-ZipBuf (int)<128+]]\n" +
                    "[-Clone 'host1[ ..hostn]']\n" +
                    "[-Deploy 'host1[ ..hostn]']\n" +
                    "$Id: SocketProxy.java,v 1.1 2002/12/08 16:05:49 grrrrr Exp $\n");
        }
        SocketProxy d = new SocketProxy(m);
    };

    public int getSourcePort() {
        if (!srcPort_i.hasNext())
            srcPort_i = srcPort.iterator();
        return ((Integer) srcPort_i.next()).intValue();
    }

    public String getSourceHost() {
        if (!srcHost_i.hasNext())
            srcHost_i = srcHost.iterator();
        return (String) srcHost_i.next();
    }

    public int getProxyPort() {
        return Integer.decode((String) get("AgentPort")).intValue();
    }

    /**
     * this has the effect of taking over the command of the http
     * service on the agent host and handling messages to marshal http registrations
     */
    public SocketProxy(Map m) {
        super(m);
        StringTokenizer t = new StringTokenizer(m.get("SourcePort").toString());

        while (t.hasMoreTokens()) {
            srcPort.add(Integer.decode(t.nextToken().toString()));
        }
        ;

        t = new StringTokenizer(m.get("SourceHost").toString());

        while (t.hasMoreTokens()) {
            srcHost.add(t.nextToken().toString());
        }
        ;
        srcPort_i = srcPort.iterator();
        srcHost_i = srcHost.iterator();

        try {
            relocate();
            ss = new ServerSocket(getProxyPort());
            new Thread(this).start();
            spin();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
        }
    }

    public void run() {
        while (!killFlag) {
            try {

                Socket inbound = ss.accept();

                PipeSocket ps = new PipeSocket(inbound, agentStreamDesc(), sourceStreamDesc());


                ps.connectTarget(new Socket((String) getSourceHost(), getSourcePort()));
                ps.spin();
            }
            catch (InterruptedIOException e) {
                if (Env.logDebug) Env.log(500, getClass().getName() + "::interrupt " + e.getMessage());
            }
            catch (Exception e) {
                if (Env.logDebug) Env.log(10, getClass().getName() + "::run " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private StreamDesc sourceStreamDesc() {
        final String[] a =  {"source", "both"};
        return new StreamDesc(
                containsKey("Inflate")?Arrays.asList(a).contains(get("Inflate")):false,
                containsKey("Deflate")?Arrays.asList(a).contains(get("Deflate")):false,
                containsKey("ZipBuf")?Integer.decode(get("ZipBuf").toString()).intValue():4096,
                containsKey("Buffer")?Integer.decode(get("Buffer").toString()).intValue():0);
    }

    private StreamDesc agentStreamDesc() {
        final String[] a =  {"agent", "both"};
        return new StreamDesc(
                containsKey("Inflate")?Arrays.asList(a).contains(get("Inflate")):false,
                containsKey("Deflate")?Arrays.asList(a).contains(get("Deflate")):false,
                containsKey("ZipBuf")?Integer.decode(get("ZipBuf").toString()).intValue():4096,
                containsKey("Buffer")?Integer.decode(get("Buffer").toString()).intValue():0);
    }

    public ServerSocket getSs() {
        return ss;
    }

    public void setSs(ServerSocket ss) {
        this.ss = ss;
    }
}
