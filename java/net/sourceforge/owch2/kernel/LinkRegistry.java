package net.sourceforge.owch2.kernel;

import java.util.*;

/**
 * Class is used to track the connections a proxy is entertaining.
 * "Update" notifications are simply LinkRegistries, with routing
 * info that gets stripped out.
 *  This is implicitly a proxy aggregate.
 * @version $Id: LinkRegistry.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
//TODO:remove LinkRegistry from owch

public class LinkRegistry extends TreeMap {
    /** Default constructor */
    public LinkRegistry() {
        super();
    }

    /**
     * C'tor with copy.
     * @param m The source,  generally an Update Notification.
     */
    public LinkRegistry(MetaProperties m) {
        super(m);
        prune();
    };

    /** Strips a Notification-derived LinkRegistry of transport tags. */
    final void prune() {
        for (int i = 0; i < reserved.length; i++) {
            String t = reserved[i];
            if (containsKey(t)) {
                remove(t);
            }
        }
    };

    /** Holds reserved Keywords that should be stripped from
     *  notifications */
    static String[] reserved =
            {"ACK".intern(), "Created".intern(), "JMSDestination".intern(), "MessageText".intern(), "JMSReplyTo".intern(), "ResentFrom".intern(), "JMSMessageID".intern(), "JMSType".intern(), "URL".intern(), "retry".intern()};
}


