package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;

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
 * @version $Id$
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
        Map<?, ?> m = Env.getInstance().parseCommandLineArgs(args);
        final String[] ka = {Message.REPLYTO_KEY, "SourcePort", "SourceHost", "AgentPort"};

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
                    "$Id$\n");
        }
        SocketProxy d = new SocketProxy(m);
    }

    public int getSourcePort() {
        if (!srcPort_i.hasNext())
            srcPort_i = srcPort.iterator();
        return srcPort_i.next().intValue();
    }

    public Object getSourceHost() {
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
            srcPort.add(Integer.decode(t.nextToken()));
        }

        t = new StringTokenizer(m.get("SourceHost").toString());

        while (t.hasMoreTokens()) {
            srcHost.add(t.nextToken());
        }
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
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private StreamDesc sourceStreamDesc() {
        final String[] a = {"source", "both"};
        return new StreamDesc(
                containsKey("Inflate") && Arrays.asList(a).contains(get("Inflate")),
                containsKey("Deflate") && Arrays.asList(a).contains(get("Deflate")),
                containsKey("ZipBuf") ? Integer.decode(get("ZipBuf").toString()).intValue() : 4096,
                containsKey("Buffer") ? Integer.decode(get("Buffer").toString()).intValue() : 0);
    }

    private StreamDesc agentStreamDesc() {
        final String[] a = {"agent", "both"};
        return new StreamDesc(
                containsKey("Inflate") && Arrays.asList(a).contains(get("Inflate")),
                containsKey("Deflate") && Arrays.asList(a).contains(get("Deflate")),
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
