package net.sourceforge.owch2.kernel;


/**
 * @version $Id: PipeFactory.java,v 1.1 2002/12/08 16:05:51 grrrrr Exp $
 * @author James Northrup
 */
public class PipeFactory extends ListenerFactory {
    /** ctor */
    public PipeFactory() {
    }

    public final MetaProperties getLocation() {
        return Env.getProtocolCache().getLocation("pipe");
    };

    public ListenerReference create(java.net.InetAddress hostAddr, int port, int threads) {
        Thread t = null;
        PipeConnector Pipes;
        try {
            Pipes = new PipeConnector(hostAddr, (int) port, threads);
        }
        catch (Exception e) {
            if (Env.logDebug) Env.log(2, "PipeConnector init failure port " + port);
            return null;
        }
        for (int i = 0; i < Pipes.getThreads(); i++) {
            t = new Thread(Pipes, "PipeListener Thread #" + i + " / port " + Pipes.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        return Pipes;
    };
}