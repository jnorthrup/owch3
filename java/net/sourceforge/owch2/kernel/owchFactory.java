package net.sourceforge.owch2.kernel;

import java.net.InetAddress;


/**
 * @author James Northrup
 * @version $Id: owchFactory.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public final class owchFactory extends ListenerFactory {
    /**
     * ctor
     */
    public owchFactory() {
        if (Env.getInstance().logDebug) Env.getInstance().log(200, "DataGramFactory instantiated");
    }

    public ListenerReference create(InetAddress hostAddr, int port, int threads) {
        Thread t = null;
        owchListener udps = null;
        try {
            udps = new owchListener(hostAddr, port, threads);
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Env.getInstance().log(2, "owchListener failure on port " + port);
            e.printStackTrace();
        }
        ;

        for (int i = 0; i < Env.getInstance().getHostThreads(); i++) {
            t = new Thread(udps, "owchListener Thread #" + i + " / port " + udps.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        ;
        return udps;
    }

    ;

    public final MetaProperties getLocation() {
        return Env.getInstance().getProtocolCache().getLocation("owch");
    }

    ;
}

;


