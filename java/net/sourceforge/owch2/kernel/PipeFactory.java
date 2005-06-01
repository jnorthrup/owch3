package net.sourceforge.owch2.kernel;


/**
 * @author James Northrup
 * @version $Id: PipeFactory.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class PipeFactory extends ListenerFactory {
    /**
     * ctor
     */
    public PipeFactory() {
    }

    public final MetaProperties getLocation() {
        return Env.getInstance().getProtocolCache().getLocation("pipe");
    }

    ;

    public ListenerReference create(java.net.InetAddress hostAddr, int port, int threads) {
        Thread t = null;
        PipeConnector Pipes;
        try {
            Pipes = new PipeConnector(hostAddr, (int) port, threads);
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Env.getInstance().log(2, "PipeConnector init failure port " + port);
            return null;
        }
        for (int i = 0; i < Pipes.getThreads(); i++) {
            t = new Thread(Pipes, "PipeListener Thread #" + i + " / port " + Pipes.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        return Pipes;
    }

    ;
}