package net.sourceforge.owch2.kernel;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * ProtocolCache
 *
 * @author James Northrup
 * @version $Id: ProtocolCache.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class ProtocolCache extends HashMap {
    public ListenerCache getListenerCache(String Protocol) {
        if (Env.getInstance().logDebug) Env.getInstance().log(20, "protocolCache.getListenerCache -- " + Protocol);
        ListenerCache lc = (ListenerCache) get(Protocol);
        if (lc == null) {
            try {
                if (Env.getInstance().logDebug) Env.getInstance().log(20, "attempting to create " + Protocol);
                String cname = "owch." + Protocol;
                String factory = Protocol + "Factory";
                if (Env.getInstance().logDebug) Env.getInstance().log(20, "attempting to register " + factory);
                lc = new ListenerCache();
                for (int i = 0; i < Env.getInstance().getSocketCount(); i++) {
                    Method m1 = Env.getInstance().getClass().getMethod("get" + factory, new Class[]{});
                    ListenerFactory lf = (ListenerFactory) m1.invoke(this,
                            new Object[ 0 ]);
                    lc.put(lf.create(Env.getInstance().getHostAddress(), (i == 0) ? Env.getInstance().getHostPort() : 0, Env.getInstance().getHostThreads()));
                }
                put(Protocol, lc);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lc;
    }

    ;

    public net.sourceforge.owch2.kernel.Location getLocation(String Protocol) {
        ListenerCache l = (ListenerCache) getListenerCache(Protocol);

        if (l != null) {
            return l.getLocation();
        }
        ;
        return null;
    }

    ;
}


