package net.sourceforge.owch2.kernel;

/**
 * Agent provides the base class which communicates with the Env
 * agent host platform and the protocols it operates. communication is
 * handled by constructing a MetaProperties Object and calling the
 * send() method of the AbstractAgent.  The Env Host platform manages the details of protocols, routing, and delivery to
 * other agents in the namespace.
 *
 * @author James Northrup
 * @version $Id: Agent.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public interface Agent extends MetaAgent, MetaPropertiesFilter {
    String RESOURCE_KEY = "Resource";

    /**
     * Gets one of this object's properties using the associated key.
     *
     * @see #putValue
     */
    Object getValue(String key);

    /**
     * Sets one of this object's properties using the associated key. If the value has
     * changed, a <code>PropertyChangeEvent</code> is sent to listeners.
     *
     * @param key   a <code>String</code> containing the key
     * @param value an <code>Object</code> value
     */
    void putValue(String key, Object value);

    boolean isParent();

    /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     *
     * @param lk node(s) to link to
     */
    void linkTo(String lk);

    /**
     * send a Notification
     *
     * @param n Notification destined for somewhere else
     */
    void send(MetaProperties n);

    /**
     * handle an incoming message presumably to this instance.
     */
    void recv(MetaProperties notificationIn);
}
