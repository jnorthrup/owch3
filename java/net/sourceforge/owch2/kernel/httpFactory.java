package net.sourceforge.owch2.kernel;


/**
 * @version $Id: httpFactory.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public class httpFactory extends ListenerFactory {
    /** ctor */
    public httpFactory() {
    }

    public MetaProperties getLocation() {
        return Env.getProtocolCache().getLocation("http");
    };

    public ListenerReference create(java.net.InetAddress hostAddr, int port, int threads) {
        Thread t = null;
        httpServer https;
        try {
            https = new httpServer(hostAddr, (int) port, threads);
        }
        catch (Exception e) {
            if (Env.logDebug) Env.log(2, "httpServer init failure port " + port);
            return null;
        }
        ;
        for (int i = 0; i < https.getThreads(); i++) {
            t = new Thread(https, "httpListener Thread #" + i + " / port " + https.getLocalPort());
            t.setDaemon(true);
            t.start();
        }
        ;
        return https;
    };
}

;


