package net.sourceforge.owch2.kernel;

import java.util.concurrent.*;
import java.util.logging.*;


/**
 * @author James Northrup
 * @version $Id: PipeFactory.java,v 1.4 2005/06/04 02:26:24 grrrrr Exp $
 */
public class PipeFactory extends ListenerFactory {
    /**
     * ctor
     */
    public PipeFactory() {
        executorService = Executors.newCachedThreadPool();
    }


    public final MetaProperties getLocation() {
        return ProtocolType.Pipe.getLocation();
    }

    public ListenerReference create() {
        Thread t = null;
        PipeConnector pipeConnector;
        try {
            pipeConnector = new PipeConnector(hostAddress, (int) port, threads);
            executorService.submit(pipeConnector);
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Logger.global.info("PipeConnector init failure port " + port);
            return null;
        }

        return pipeConnector;
    }

}