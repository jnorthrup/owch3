package net.sourceforge.owch2.kernel;

import java.net.*;


/**
 * @version $Id: owchFactory.java,v 1.1 2002/12/08 16:05:51 grrrrr Exp $
 * @author James Northrup
 */
public final class owchFactory extends ListenerFactory {
    /** ctor */
    public owchFactory() {
        if (Env.logDebug) Env.log(200, "DataGramFactory instantiated");
    }

    public ListenerReference create( InetAddress hostAddr, int port, int threads) {
        Thread t = null;
        owchListener udps = null;
        try {
            udps = new owchListener(hostAddr, port, threads);
        }
        catch (Exception e) {
            if (Env.logDebug) Env.log(2, "owchListener failure on port " + port);
            e.printStackTrace();
        }        ;

        for (int i = 0; i < Env.getHostThreads(); i++) {
            t = new Thread(udps, "owchListener Thread #" + i + " / port " + udps.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        ;
        return udps;
    };

    public final MetaProperties getLocation() {
        return Env.getProtocolCache().getLocation("owch");
    };
}

;


