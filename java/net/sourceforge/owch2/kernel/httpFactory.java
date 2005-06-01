package net.sourceforge.owch2.kernel;


/**
 * @author James Northrup
 * @version $Id: httpFactory.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class httpFactory extends ListenerFactory {
    /**
     * ctor
     */
    public httpFactory() {
    }

    public MetaProperties getLocation() {
        return Env.getInstance().getProtocolCache().getLocation("http");
    }

    public ListenerReference create(java.net.InetAddress hostAddr, int port, int threads) {
        Thread t = null;
        httpServer https;
        try {
            https = new httpServer(hostAddr, (int) port, threads);
        }
        catch (Exception e) {
            if (Env.getInstance().logDebug) Env.getInstance().log(2, "httpServer init failure port " + port);
            return null;
        }
        for (int i = 0; i < https.getThreads(); i++) {
            t = new Thread(https, "httpListener Thread #" + i + " / port " + https.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        return https;
    }

    ;
}

;


