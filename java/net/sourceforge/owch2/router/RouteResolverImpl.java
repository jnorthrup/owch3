package net.sourceforge.owch2.router;

import java.util.*;
import java.util.logging.*;

/**
 * @author James Northrup
 * @version $Id: RouteResolverImpl.java,v 1.2 2005/06/03 18:27:47 grrrrr Exp $
 */
abstract public class RouteResolverImpl implements RouteResolver {
    public static final String REPLYTO_KEY = "JMSReplyTo";
    public static final String DESTINATION_KEY = "JMSDestination";

    public void send(Map<String, ?> item) {
        if (item.get(REPLYTO_KEY) == null) {
            Logger.getAnonymousLogger().info("*** dropping nameless message");
            return;
        }
        if (item.get(REPLYTO_KEY) == item.get(DESTINATION_KEY)) {
            Logger.getAnonymousLogger().info("*** dropping routeless");
            return;
        }

        boolean sated;

        for (Router router : getOutbound()) {
            Logger.getAnonymousLogger().info("***  testing " + item.toString());
            Object dest = router.getDestination(item);
            sated = router.hasPath((String) dest);
            if (sated) {
                router.send(item);
                break;
            }
        }

        for (Router router : getOutbound()) {
            if (router.pathExists(item)) {
                break;
            }
        }
    }

    public void remove(String key) {
        for (Router r : getInbound())
            r.remove((String) key);

    }
}


