package net.sourceforge.owch2.kernel;

import java.util.concurrent.*;


/**
 * @author James Northrup
 * @version $Id$
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
            return null;
        }

        return pipeConnector;
    }

}