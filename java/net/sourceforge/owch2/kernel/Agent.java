package net.sourceforge.owch2.kernel;

/**
 * Agent provides the base class which communicates with the Env
 * agent host platform and the protocols it operates. communication is
 * handled by constructing a EventDescriptor Object and calling the
 * route() method of the AbstractAgent.  The Env Host platform manages the details of protocols, routing, and delivery to
 * other agents in the namespace.
 *
 * @author James Northrup
 * @version $Id$
 */
public interface Agent<V> {
    String RESOURCE_KEY = "Resource";


    boolean isParent();

    /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     *
     * @param lk node(s) to link to
     */
    void linkTo(String lk);

    /**
     * route a EventDescriptor
     *
     * @param n EventDescriptor destined for somewhere else
     */
    void send(EventDescriptor n);

    /**
     * handle an incoming message presumably to this instance.
     */
    void recv(EventDescriptor notificationIn);


    V getValue(String key);

    void putValue(String key, V value);
}
