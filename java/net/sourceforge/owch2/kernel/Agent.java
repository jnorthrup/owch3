package net.sourceforge.owch2.kernel;

/**
 * Agent provides the base class which communicates with the Env
 * agent host platform and the protocols it operates. communication is
 * handled by constructing a MetaProperties Object and calling the
 * send() method of the AbstractAgent.  The Env Host platform manages the details of protocols, routing, and delivery to
 * other nodes in the namespace.
 * @version $Id: Agent.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public interface Agent extends MetaAgent, MetaPropertiesFilter {
    /**
     * Gets one of this object's properties using the associated key.
     * @see #putValue
     */
    Object getValue(String key);

    /**
     * Sets one of this object's properties using the associated key. If the value has
     * changed, a <code>PropertyChangeEvent</code> is sent to listeners.
     * @param key    a <code>String</code> containing the key
     * @param value  an <code>Object</code> value
     */
    void putValue(String key, Object value);

    boolean isParent();

    /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     * @param lk node(s) to link to
     */
    void linkTo(String lk);

    /** send a Notification
     * @param n  Notification destined for somewhere else
     */
    void send(MetaProperties n);

    /**handle an incoming message presumably to this instance.*/
    void recv(MetaProperties notificationIn);
}
