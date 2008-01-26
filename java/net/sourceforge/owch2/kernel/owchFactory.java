package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;
import java.util.logging.*;


/**
 * @author James Northrup
 * @version $Id: owchFactory.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public final class owchFactory extends ListenerFactory {
    private static owchFactory instance;

    public owchFactory(InetAddress hostAddress, int port, int threads) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.threads = threads;
    }

    /**
     * ctor
     */
    public owchFactory() {
        Logger.getAnonymousLogger().info("DataGramFactory instantiated");
    }

    public ListenerReference create() {


        Thread t = null;
        owchListener udps = null;
        try {
            udps = new owchListener(hostAddress, port, threads);
        }
        catch (IOException e) {
            Logger.getAnonymousLogger().info("owchListener failure on port " + port);
            e.printStackTrace();
        }

        for (int i = 0; i < getThreads(); i++) {
            t = new Thread(udps, "owchListener Thread #" + i + " / port " + udps.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        return udps;
    }

    public final MetaProperties getLocation() {

        return ProtocolType.owch.getLocation();
    }

}
