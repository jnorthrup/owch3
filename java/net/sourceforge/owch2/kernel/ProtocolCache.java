package net.sourceforge.owch2.kernel;

import java.lang.reflect.*;
import java.util.*;

/**
 *
 * ProtocolCache
 *
 * @version $Id: ProtocolCache.java,v 1.1 2002/12/08 16:05:51 grrrrr Exp $
 * @author James Northrup
 */
public class ProtocolCache extends HashMap {
    public ListenerCache getListenerCache(String Protocol) {
        if (Env.logDebug) Env.log(20, "protocolCache.getListenerCache -- " + Protocol);
        ListenerCache lc = (ListenerCache) get(Protocol);
        if (lc == null) {
            try {
                if (Env.logDebug) Env.log(20, "attempting to create " + Protocol);
                String cname = "owch." + Protocol;
                String factory = Protocol + "Factory";
                if (Env.logDebug) Env.log(20, "attempting to register " + factory);
                lc = new ListenerCache();
                for (int i = 0; i < Env.getSocketCount(); i++) {
                    Method m1 = Env.class.getMethod("get" + factory,
                                                    new Class[]{});
                    ListenerFactory lf = (ListenerFactory) m1.invoke(this,
                                                                     new Object[ 0 ]);
                    lc.put(lf.create(Env.getHostAddress(), (i == 0)?    Env.getHostPort() :0 , Env.getHostThreads()));
                }
                put(Protocol, lc);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return lc;
    };

    public net.sourceforge.owch2.kernel.Location getLocation(String Protocol) {
        ListenerCache l = (ListenerCache) getListenerCache(Protocol);

        if (l != null) {
            return l.getLocation();
        }
        ;
        return null;
    };
}


