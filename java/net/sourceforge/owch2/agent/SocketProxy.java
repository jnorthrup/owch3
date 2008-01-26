package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * SocketProxy
 * <p/>
 * connects incoming sockets to outbound host,port destinations.
 * <p/>
 * note:
 * hosts and ports can be specified 1:1, 1:n, n:1 or n:n.
 * <p/>
 * lists of ports and hosts are tried in order specified.
 *
 * @version $Id: SocketProxy.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class SocketProxy extends AbstractAgent implements Runnable {
    private Collection<Integer> srcPort = new ArrayList<Integer>();
    private Collection<String> srcHost = new ArrayList<String>();
    private Iterator<Integer> srcPort_i;
    private Iterator<String> srcHost_i;
    String AgentPort;
    private ServerSocket ss;
    private PipeFactory pf = new PipeFactory();

    public static void main(String[] args) {
        Map<? extends Object, ? extends Object> m = Env.getInstance().parseCommandLineArgs(args);
        final String[] ka = {"JMSReplyTo", "SourcePort", "SourceHost", "AgentPort"};

        if (!m.keySet().containsAll(Arrays.asList(ka))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
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
                    "$Id: SocketProxy.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $\n");
        }
        SocketProxy d = new SocketProxy(m);
    }

    ;

    public int getSourcePort() {
        if (!srcPort_i.hasNext())
            srcPort_i = srcPort.iterator();
        return (srcPort_i.next()).intValue();
    }

    public String getSourceHost() {
        if (!srcHost_i.hasNext())
            srcHost_i = srcHost.iterator();
        return srcHost_i.next();
    }

    public int getProxyPort() {
        return Integer.decode((String) get("AgentPort")).intValue();
    }

    /**
     * this has the effect of taking over the command of the Http
     * service on the agent host and handling messages to marshal Http registrations
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
    }

    ;

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
                if (false)
                    Logger.getAnonymousLogger().info(getClass().getName() + "::interrupt " + e.getMessage());
            }
            catch (Exception e) {
                if (false)
                    Logger.getAnonymousLogger().info(getClass().getName() + "::run " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private StreamDesc sourceStreamDesc() {
        final String[] a = {"source", "both"};
        return new StreamDesc(
                containsKey("Inflate") ? Arrays.asList(a).contains(get("Inflate")) : false,
                containsKey("Deflate") ? Arrays.asList(a).contains(get("Deflate")) : false,
                containsKey("ZipBuf") ? Integer.decode(get("ZipBuf").toString()).intValue() : 4096,
                containsKey("Buffer") ? Integer.decode(get("Buffer").toString()).intValue() : 0);
    }

    private StreamDesc agentStreamDesc() {
        final String[] a = {"agent", "both"};
        return new StreamDesc(
                containsKey("Inflate") ? Arrays.asList(a).contains(get("Inflate")) : false,
                containsKey("Deflate") ? Arrays.asList(a).contains(get("Deflate")) : false,
                containsKey("ZipBuf") ? Integer.decode(get("ZipBuf").toString()).intValue() : 4096,
                containsKey("Buffer") ? Integer.decode(get("Buffer").toString()).intValue() : 0);
    }

    public ServerSocket getSs() {
        return ss;
    }

    public void setSs(ServerSocket ss) {
        this.ss = ss;
    }
}
