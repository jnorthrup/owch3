package net.sourceforge.owch2.protocol.router;

import net.sourceforge.owch2.kernel.*;
import net.sourceforge.owch2.protocol.*;
import static net.sourceforge.owch2.protocol.Transport.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Glamdring Incorporated Enterprises.  All rights reserved.
 * User: jim
 * Date: Jan 27, 2008
 * Time: 6:10:22 AM
 */
public class ipcRouter implements Router {
    private ConcurrentHashMap<String, URI> pathMap = new ConcurrentHashMap<String, URI>();
    private ConcurrentHashMap<String, Agent> localAgents = new ConcurrentHashMap<String, Agent>();


    public ConcurrentMap<String, URI> getPathMap() {
        return pathMap;
    }

    public URI getPath(EventDescriptor destination) {
        return pathMap.get(destination);
    }

    public boolean hasPath(EventDescriptor location) {
        return pathMap.containsKey(location.getDestination());
    }

    /**
     * sends a variable number of events in the best attempt  to retain the sequence.
     *
     * @param async
     * @return
     * @throws Exception
     */
    public Future<Receipt> send(final EventDescriptor... async) throws Exception {
        final Callable<Receipt> sender = new Callable<Receipt>() {

            public Receipt call() throws Exception {

                return new Receipt() {
                    public Transport getTransport() {
                        return ipc;
                    }

                    public Iterator<EventDescriptor> iterator() {
                        return Arrays.asList(async).iterator();
                    }
                };
            }
        };

        return Reactor.getThreadPool().submit(sender);
    }

    public URI remove(String jmsReplyTo) {
        return (URI) localAgents.remove(jmsReplyTo).getValue(EventDescriptor.URI_KEY);
    }

    public ConcurrentHashMap<String, Agent> getLocalAgents() {
        return localAgents;
    }
}