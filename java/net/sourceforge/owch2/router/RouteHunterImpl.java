package net.sourceforge.owch2.router;

import java.util.*;
import java.util.logging.*;

/**
 * @author James Northrup
 * @version $Id: RouteHunterImpl.java,v 1.2 2005/06/03 18:27:47 grrrrr Exp $
 */
abstract public class RouteHunterImpl implements RouteHunter {
    public static final String REPLYTO_KEY = "JMSReplyTo";
    public static final String DESTINATION_KEY = "JMSDestination";

    public void send(Map item) {
        if (item.get(REPLYTO_KEY) == null) {
            Logger.global.info("*** dropping nameless message");
            return;
        }
        if (item.get(REPLYTO_KEY) == item.get(DESTINATION_KEY)) {
            Logger.global.info("*** dropping routeless");
            return;
        }

        boolean sated = false;

        for (Router router : getOutbound()) {
            Logger.global.info("***  testing " + item.toString());
            Object dest = router.getDestination(item);
            sated = router.hasElement(dest);
            if (sated) {
                router.send(item);
                break;
            }
        }

        for (Router router : getOutbound()) {
            if (router.proxyAccepted(item)) {
                break;
            }
        }
    }

    public void remove(Object key) {
        for (Router r : getInbound())
            r.remove(key);

    }
}


