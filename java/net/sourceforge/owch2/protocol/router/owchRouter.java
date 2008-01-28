package net.sourceforge.owch2.protocol.router;

import net.sourceforge.owch2.kernel.*;
import net.sourceforge.owch2.protocol.*;

import java.util.concurrent.*;

/**
 * Glamdring Incorporated Enterprises.  All rights reserved.
 * User: jim
 * Date: Jan 27, 2008
 * Time: 6:12:57 AM
 */
public class owchRouter extends AbstractRouterImpl {

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
    }

    @Override
    public Future<Reciept> send(EventDescriptor... async) {
        super.send(async);    //Todo: verify for a purpose
    }
}


