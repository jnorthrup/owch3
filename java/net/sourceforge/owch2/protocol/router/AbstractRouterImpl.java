package net.sourceforge.owch2.protocol.router;

import net.sourceforge.owch2.kernel.EventDescriptor;
import net.sourceforge.owch2.protocol.Transport;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author James Northrup
 * @version $Id: owchRouter.java 10 2008-01-26 09:39:53Z grrrrr $
 */
public abstract class AbstractRouterImpl implements Router {
    private ConcurrentMap<String, URI> pathMap = new ConcurrentHashMap<String, URI>();
    protected Transport transport;

    public AbstractRouterImpl(Transport transport) {
        this.transport = transport;
    }

    public ConcurrentMap<String, URI> getPathMap() {
        return pathMap;
    }

    public URI getPath(EventDescriptor dest) {
        String destination = dest.getJMSReplyTo();
        return getPathMap().get(null != destination ? destination : dest.getURI());
    }

    public boolean hasPath(EventDescriptor location) {
        return pathMap.containsKey(location);
    }

    public URI remove(String jmsReplyTo) {
        return pathMap.remove(jmsReplyTo);
    }

}