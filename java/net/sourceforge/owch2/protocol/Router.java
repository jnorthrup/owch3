package net.sourceforge.owch2.protocol;

import net.sourceforge.owch2.kernel.*;

import java.io.*;
import java.lang.ref.*;
import java.util.*;

/**
 * router assembles gateways and pathways to deliver to JMSDestinations reachable over xyz transport
 * <p/>
 * User: jim
 * Date: Jan 26, 2008
 * Time: 3:09:58 PM
 */
public interface Router {
    /**
     * provides agent->router links on delivery
     *
     * @return a ref to the routes currently held by this router instance
     */
    Reference<Map> getPathMap();

    /**
     * retreives or builds a path for a Agent
     *
     * @param destination agent name
     * @return Location with Uri
     */
    Location getPath(MetaAgent destination);

    /**
     * tells resolvers whether we are gonig to resolve this
     * path or to keep going
     *
     * @param location needs URL if possible...
     * @return whether we will route that message
     */
    boolean hasPath(MetaAgent location);

    /**
     * "OneWay" communication handler, to fire and forget,
     * used in realtime udp or any other message desired
     *
     * @param async simple poerties ...
     */
    void send(Message... async);

    /**
     * domain-specific log
     *
     * @param logged simple properties
     * @return a protocol specific logging entry
     */
    Serializable sendWithLog(Message... logged);

    /**
     * provides the reciept and its state updates may be polled/watched etc.
     *
     * @param synMessages synchronous message
     * @return Receipt instance
     */
    Reciept sendWithReceipt(Message... synMessages);

    /**
     * @param syncMessages messages
     * @return a transient observable object
     */
    Reference<Observable> sendWithNotification(Message... syncMessages);
}
