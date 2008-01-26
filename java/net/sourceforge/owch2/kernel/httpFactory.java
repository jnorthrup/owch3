package net.sourceforge.owch2.kernel;

/**
 * @author James Northrup
 * @version $Id$
 */
public class httpFactory extends ListenerFactory {
    /**
     * ctor
     */
    public httpFactory() {
    }

    public MetaProperties getLocation() {

        return ProtocolType.Http.getLocation();
    }

    public ListenerReference create() {
        Thread t = null;
        httpServer https;
        try {
            https = new httpServer(hostAddress, (int) port, threads);
        }
        catch (Exception e) {
            return null;
        }
        for (int i = 0; i < https.getThreads(); i++) {
            t = new Thread(https, "httpListener Thread #" + i + " / port " + https.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        return https;
    }

    private static httpFactory instance = new httpFactory();

    public static httpFactory getInstance() {
        return instance;
    }
}



