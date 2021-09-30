package net.sourceforge.owch2.kernel;

/**
 * Agent provides the base class which communicates with the Env
 * agent host platform and the protocols it operates. communication is
 * handled by constructing a Notification Object and calling the
 * route() method of the AbstractAgent.  The Env Host platform manages the details of protocols, routing, and delivery to
 * other agents in the namespace.
 *
 * @author James Northrup
 * @version $Id$
 */
public interface Agent extends HasOrigin, HasProperties/*, Invocable*/ {

    boolean isParent();

    /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     *
     * @param lk node(s) to link to
     */
    void linkTo(String lk);

    /**
     * route a Notification
     *
     * @param n Notification destined for somewhere else
     */
    void send(Transaction n);

    /**
     * handle an incoming message presumably to this instance.
     */
    void recv(Notification notificationIn);

    Object getValue(CharSequence key);

    void putValue(String key, Object value);
}
