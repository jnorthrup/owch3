package net.sourceforge.owch2.protocol.router;

import net.sourceforge.owch2.kernel.EventDescriptor;
import net.sourceforge.owch2.protocol.Transport;

/**
 * Glamdring Incorporated Enterprises.  All rights reserved.
 * User: jim
 * Date: Jan 27, 2008
 * Time: 6:12:57 AM
 */
public class owchRouter extends AbstractRouterImpl {

    public owchRouter(Transport transport) {
        super(transport);
    }

    /**
     * this router is "sticky".
     * <p/>
     * all messages via owch pass through the same router for either direction, including ACK notes.  we record each name/URI pair and update them if they already exist.
     * <p/>
     * Routing resource usage is "Discardable"
     *
     * @param eventDescriptor name of sendee
     * @return bool: do we have the path for the Destination?
     */
    @Override
    public boolean hasPath(EventDescriptor eventDescriptor) {
        return false;
    }
}


