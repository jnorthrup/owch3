package net.sourceforge.owch2.protocol.router;

import net.sourceforge.owch2.kernel.EventDescriptor;
import net.sourceforge.owch2.protocol.Receipt;

import java.net.URI;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;


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
    ConcurrentMap<String, URI> getPathMap();

    /**
     * retreives or builds a path for a Agent
     *
     * @param eventDescriptor agent name
     * @return EventDescriptor with Uri
     */
    URI getPath(EventDescriptor eventDescriptor);

    /**
     * tells resolvers whether we are gonig to resolve this
     * path or to keep going
     *
     * @param location needs URL if possible...
     * @return whether we will route that message
     */
    boolean hasPath(EventDescriptor location);

    /**
     * "OneWay" communication handler, to fire and forget,
     * used in realtime udp or any other message desired
     *
     * @param async simple poerties ...
     */
    Future<Receipt> route(EventDescriptor... async) throws Exception;

    URI remove(String jmsReplyTo);
}