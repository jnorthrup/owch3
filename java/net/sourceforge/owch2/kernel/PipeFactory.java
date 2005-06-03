package net.sourceforge.owch2.kernel;

import java.util.logging.*;


/**
 * @author James Northrup
 * @version $Id: PipeFactory.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class PipeFactory extends ListenerFactory {
    private static PipeFactory instance;

    /**
     * ctor
     */
    public PipeFactory() {
    }


    public final MetaProperties getLocation() {

        return ProtocolType .Pipe.getLocation();
    }

    public ListenerReference create() {
        Thread t = null;
        PipeConnector Pipes;
        try {
            Pipes = new PipeConnector(hostAddress, (int) port, threads);
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Logger.global.info("PipeConnector init failure port " + port);
            return null;
        }
        for (int i = 0; i < Pipes.getThreads(); i++) {
            t = new Thread(Pipes, "PipeListener Thread #" + i + " / port " + Pipes.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        return Pipes;
    }

    public static PipeFactory getInstance() {
        if (instance == null) instance = new PipeFactory();
        return instance;
    }
}