package net.sourceforge.owch2.kernel;

import java.util.logging.*;


/**
 * @author James Northrup
 * @version $Id: httpFactory.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class httpFactory extends ListenerFactory {
    /**
     * ctor
     */
    public httpFactory() {
    }

    public MetaProperties getLocation() {

        Location location = ProtocolType.Http.getLocation();
        return location;
    }

    public ListenerReference create() {
        Thread t = null;
        httpServer https;
        try {
            https = new httpServer(hostAddress, (int) port, threads);
        }
        catch (Exception e) {
            if (false) Logger.getAnonymousLogger().info("httpServer init failure port " + port);
            return null;
        }
        for (int i = 0; i < https.getThreads(); i++) {
            t = new Thread(https, "httpListener Thread #" + i + " / port " + https.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        return https;
    }

    private static httpFactory instance;

    public static httpFactory getInstance() {
        if (instance == null) instance = new httpFactory();
        return instance;
    }
}



