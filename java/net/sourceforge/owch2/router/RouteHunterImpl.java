package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.Env;

import java.util.Iterator;
import java.util.Map;

/**
 * @author James Northrup
 * @version $Id: RouteHunterImpl.java,v 1.1 2005/06/01 06:43:12 grrrrr Exp $
 */
abstract public class RouteHunterImpl implements RouteHunter {
    public void send(Map item) {
        if (item.get("JMSReplyTo") == null) {
            if (Env.getInstance() .logDebug) Env.getInstance().log(500, "*** dropping nameless message");
            return;
        }
        if (item.get("JMSReplyTo") == item.get("JMSDestination")) {
            if (Env.getInstance().logDebug) Env.getInstance().log(500, "*** dropping routeless");
            return;
        }
        boolean sated = false;
        for (Iterator<Router> i = getOutbound().iterator(); i.hasNext();) {
            Router router = i.next();
            if (Env.getInstance().logDebug)
                Env.getInstance().log(500, "***" + router.getClass().getName() + " testing " + item.toString() + "");
            Object dest = router.getDestination(item);
            sated = router.hasElement(dest);
            if (sated) {
                router.send(item);
                break;
            }
        }
        ;
        for (Iterator<Router> i = getOutbound().iterator(); i.hasNext();) {
            Router router = i.next();
            if (router.addElement(item)) {
                break;
            }
        }
    }

    ;

    public void remove(Object key) {
        for (Iterator<Router> i = getInbound().iterator(); i.hasNext();) {
            Router r = i.next();
            r.remove(key);
        }
    }

    ;
}

;


