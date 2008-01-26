package net.sourceforge.owch2.router;

import java.util.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public interface Router {
    public void remove(String key);

    Set getPool();

    /**
     * deliver this message on its way.
     *
     * @param item has a JMSDestination
     */
    void send(Map<String, ?> item);

    /**
     * asks the router where this message is going.
     *
     * @param item a Message
     * @return a Location which may or may not be a routed answer or a direct delivery.
     */
    String getDestination(Map<String, ?> item);

    /**
     * Router is asked to resolve a path.
     *
     * @param item a Location
     * @return the Router resolves this Location
     */
    boolean pathExists(Map<String, ?> item);

    boolean hasPath(String key);
}


