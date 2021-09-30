package net.sourceforge.owch2.kernel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;
import static net.sourceforge.owch2.kernel.AgentLifecycle.*;
import static net.sourceforge.owch2.protocol.TransportEnum.http;

/**
 * AbstractAgent provides the base class which communicates with the Env
 * agent host platform and the protocols it operates. communication is
 * handled by constructing a Notification Object and calling the
 * route() method of the AbstractAgent.  The Env Host platform manages the details of protocols, routing, and delivery to
 * other nodes in the namespace.
 *
 * @author James Northrup
 * @version $Id$
 */
public abstract class AbstractAgent extends LinkedHashMap<CharSequence, Object> implements Agent {
    CharSequence RESOURCE_KEY = "Resource";
    CharSequence MOBILEHOST_KEY = "Host";
    public static final CharSequence TYPE_KEY = "JMSType";
    public static final CharSequence PRIORITY_KEY = "Priority";
    public static final CharSequence SOURCE_KEY = "Source";
    public static final CharSequence DEPLOY_KEY = "Deploy";

    CharSequence DEFAULT_LINK_NAME = "default";
    String DEPLOYNODE_TYPE = DeployNode.toString();
    String UNLINK_TYPE = UnLink.toString();
    String CLONE_KEY = Clone.toString();
    String UPDATED_TYPE = Updated.toString();
    String UPDATE_TYPE = Update.toString();
    String LINK_TYPE = Link.toString();
    /**
     * agent specific old-style thread spinning would use this to stop spinning...
     */
    public boolean killFlag = false;
    boolean virgin;
//    LinkRegistry acl = null;

    private final static Class[] cls_m = new Class[]{ImmutableNotification.class};

    private static final Class[] no_class = new Class[0];
    private static final Object[] no_Parm = new Object[0];
    protected static final String CLASSTYPE_KEY = "Class";

    protected AbstractAgent(Iterable<Map.Entry<CharSequence, Object>> l) {
        this(l.iterator());
    }

    protected AbstractAgent(Map<CharSequence, Object> proto) {
        super(proto);
        start();
    }

    public AbstractAgent(Iterator<Map.Entry<CharSequence, Object>> entryIterator) {
        super(getMap(entryIterator));
        start();
    }

    private void start() {
        if (null == getFrom())
            throw new IllegalArgumentException("missing Map Entry: " + FROM_KEY);
        Env.getLocalAgents().put(getFrom(), this);
        if (!isParent())
            linkTo(DEFAULT_LINK_NAME);
    }

    /**
     * Gets one of this object's properties using the associated key.
     *
     * @see #putValue
     */
    public Object getValue(String key) {
        Object value;
        Class c = this.getClass();
        try {
            value = c.getField(key).get(this);
        }
        catch (Exception e) {
            try {
                Method m = c.getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), no_class);
                value = m.invoke(this, no_Parm);
            }
            catch (Exception e1) {
                value = get(key);
            }
        }
        return value;
    }


    /**
     * Sets one of this object's properties using the associated key.
     * <p/>
     * If the value has changed, a <code>PropertyChangeEvent</code>
     * is sent to listeners.
     *
     * @param key   a <code>String</code> containing the key
     * @param value an <code>Object</code> value
     */

    public void putValue(String key, Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
        String attempt = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);

        Class c = this.getClass();
        Class[] vclass = new Class[]{value.getClass()};
        try {
            Method m = c.getMethod(attempt, vclass);
            m.invoke(this, value);
        }
        catch (NoSuchMethodException nsm) {
            nsm.printStackTrace();
            try {
                Field f = c.getField(key);
                f.set(this, value);
            }
            catch (Exception e) {
                this.put(key, value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    public boolean isParent() {
        return false; //
    }

    /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     *
     * @param lk node(s) to link to
     */
    public void linkTo(String lk) {

    }

    /**
     * Sends a Link notification other node(s) intended to
     * establish direct socket communication.
     *
     * @param linkDestination node(s) to link to
     */
    public void linkTo(CharSequence linkDestination) {
        if (linkDestination == null) {
            Logger.getAnonymousLogger().info(".link invoked. routing to default");
            linkDestination = Env.getInstance().getParentNode().getFrom();
        }
        DefaultMapTransaction n = new DefaultMapTransaction(getFrom(), linkDestination);
        n.put(DESTINATION_KEY, linkDestination);
        n.put(TYPE_KEY, LINK_TYPE);
        send(n);
    }

    /**
     * AbstractAgent level Notification insignia creation and inter-process notification routing.
     *
     * @param n Notification destined for somewhere else
     */
    public void send(Transaction n) {
        Env.getInstance().send(n);
    }

    public void recv(Notification notificationIn) {
        try {
            Map map = getMap(notificationIn);
            String msgType = (String) map.get(TYPE_KEY);
            //for later optimizations.... switch(  valueOf(msgType)) ... is enabled.
            if (msgType != null) getClass().getMethod("handle_" + msgType, cls_m).invoke(this, notificationIn);
        }
        catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public Object getValue(CharSequence key) {
        return get(key);
    }

    protected AbstractAgent(Map.Entry<CharSequence, Object>... proto) {
        super(getMap(proto));
        start();
    }

    public void init(Map<String, Object> proto) {
        putAll(proto);
        //Transport.ipc.hasPath(this.getFrom());
        if (!isParent())
            linkTo(DEFAULT_LINK_NAME);
    }

    /**
     * this tells our (potentially clone) agent to stop re-registering. it will cease to spin.
     */
    public void handle_Dissolve(HasProperties n) {
        killFlag = true;
    }

    /**
     * link does not necessarily imply a workflow state-- Link establishes routing updates to/from the destination.
     * <p/>
     * Link will therefore provide the most direct route when possible, removing application-level routing hops.
     *
     * @param p the link initiation
     */
    public void handle_Link(Notification p) {
        CharSequence dest = p.getFrom();
        DefaultMapTransaction n = new DefaultMapTransaction(getFrom(), getURI(), dest, iterator());
        n.put(TYPE_KEY, UPDATE_TYPE);

        send(n);
        Logger.getAnonymousLogger().info(getClass().getName() + "::" + getFrom() + " AbstractAgent.update() sent for " + dest);
    }


    /**
     * Sends an update to another AbstractAgent.  this serves to improve lazy routing accuracy.
     * <p/>
     * a great number of features derive from competitiver updates for multiple nodes with 1 name
     * <p/>
     * <h3>for aggregates nodes sharing an "Alias, Cloning, or load/distance balancing  </h3>
     * <ul><li>asynchronous agent job queues: agents update more frequently as thier idles cycles increase so that they can be the chosen designated resource as the most recent update.
     * <li> facilitates the ability to deploy and clone without additional design specialization
     * </ul>
     * <p/>
     * <h3>for singletons and the dynamics of each agent whether alone or en masse</h3>
     * <ul>
     * <li> facilitates the ability to relocate a singleton agent and reroute messages held up during relocation
     * <li> can cycle or coexist addresses for multihoming, multiprotocol, and and port load-balancing
     *
     * @param p FROM_KEY
     */
    public void handle_Update(Notification p) {
        CharSequence dest = p.getFrom();
        final DefaultMapTransaction n = new DefaultMapTransaction(getFrom(), getURI(), dest, iterator());
        n.put(TYPE_KEY, UPDATED_TYPE);
        send(n);
        Logger.getAnonymousLogger().info(getClass().getName() + "::" + getFrom() + " AbstractAgent.update() sent for " + dest);
    }

    /**
     * this halts updates between the participants.  Agent relocation does not benefit from unlink.
     * queueing will await the next successful update to deliver the backlog that occurs while in transit.
     *
     * @param m node(s) to unlink to
     */
    public void handle_Unlink(Notification m) {
        CharSequence lk = m.getFrom();
        if (lk == null) {

            lk = Env.getInstance().getParentNode().getFrom();
        }

        DefaultMapTransaction n = new DefaultMapTransaction(getFrom(), getURI(), lk, iterator());
        n.put(DESTINATION_KEY, lk);
        n.put(TYPE_KEY, UNLINK_TYPE);
        send(n);
    }

    public final URI getURI() {
        return (URI) get(URI_KEY);

    }

    public CharSequence getFrom() {
        return (String) get(FROM_KEY);
    }

    public void setFrom(String val) {
        put(FROM_KEY, val);
    }

    /**
     * move - clone self.<OL><LI> node arrives at new host,
     * registers in Nodecache on opaque name. <LI>sends
     * "Dissolve" message and registers original
     * name.  <LI>Dissolver registers new opaque name,
     * informs new clone to register.<LI> awaits
     * dissolve.
     *
     * @param n the payload describing the move
     */
    public void handle_Move(Notification n) {
        final Map<CharSequence, Object> map = getMap(n);
        CharSequence host = (CharSequence) map.get(MOBILEHOST_KEY);
        if (host == null) {
            host = Env.getInstance().getHostname();
        }
        //if (Env.logDebug) Env.log(50, "Env.getURI - " + Transport);

        DefaultMapTransaction response = new DefaultMapTransaction();
        response.put(URI_KEY, http.getURI());
        response.putAll(this);
        response.put(TYPE_KEY, DEPLOYNODE_TYPE);
        response.put(CLASSTYPE_KEY, getClass().getName());
        response.put(FROM_KEY, getFrom());

        response.put(SOURCE_KEY, http.getURI().toASCIIString() + get(RESOURCE_KEY));

        response.put(DESTINATION_KEY, host);
        send(response);
        killFlag = true;

    }

    public Map<CharSequence, Object> getMap(Iterable<Map.Entry<CharSequence, Object>> n) {
        return getMap(n.iterator());

    }

    public void clone_state1(String host) {

        DefaultMapTransaction response = new DefaultMapTransaction(getFrom(), http.getURI(), host, iterator());
        response.putAll(this);
        response.put(TYPE_KEY, DEPLOYNODE_TYPE);
        response.put(CLASSTYPE_KEY, getClass().getName());
        response.put(FROM_KEY, getFrom());
        //if (Env.logDebug) Env.log(50, "Env.getURI - " + Transport);

        response.put(SOURCE_KEY, http.getURI().toASCIIString() + get(RESOURCE_KEY));
        //resource remains constant in this incarnation
        //n2.put( "Resource",get("Resource"));//produces 3 Strings
        send(response);
    }


    /**
     * clone <OL><LI>recv order to clone, and host<LI>  deploy
     * new class.  <LI>deliver content.  <LI>close channel.
     *
     * @param n clone instructions
     */
    public void handle_Clone(Notification n) {
        final Map<CharSequence, Object> map = getMap(n.iterator());
        String host = map.get(MOBILEHOST_KEY).toString(); //name of a Deploy agent
        if (host == null) {
            host = Env.getInstance().getHostname();
        }
        clone_state1(host);
    }

    /**
     * intended for automatic maintenance.. so to speak, for survival
     * tasks.. period replication, metrics evalutation, etc.  lots o TBD in this method
     */
    public void relocate() {
        if (containsKey(CLONE_KEY)) {
            String clist = (String) get(CLONE_KEY);
            remove(CLONE_KEY);
            Logger.getAnonymousLogger().info(getClass().getName() + " **Cloning for " + clist);
            StringTokenizer st = new StringTokenizer(clist);
            while (st.hasMoreTokens()) {
                clone_state1(st.nextToken());
            }
        }
        if (containsKey(DEPLOY_KEY)) {
            try {
                String clist = (String) get(DEPLOY_KEY);
                remove(DEPLOY_KEY);
                StringTokenizer st = new StringTokenizer(clist);

                while (st.hasMoreTokens()) {
                    clone_state1(st.nextToken());
                }
                sleep(15 * 1000); //kludge,
                System.exit(0); //TODO: allow our host to persist
            } catch (InterruptedException e) {
                e.printStackTrace();  //!TODO: review for fit
            }
        }
    }

    public static Map<CharSequence, Object> getMap(Iterator<Map.Entry<CharSequence, Object>> n) {

        final Map<CharSequence, Object> hashMap = new LinkedHashMap<CharSequence, Object>();

        while (n.hasNext()) {
            Map.Entry<CharSequence, Object> charSequenceObjectEntry = n.next();
            hashMap.put(charSequenceObjectEntry.getKey(), charSequenceObjectEntry.getValue());
        }
        return hashMap;
    }

    public static Map<CharSequence, Object> getMap(Map.Entry<CharSequence, Object>... n) {

        final HashMap<CharSequence, Object> hashMap = new LinkedHashMap<CharSequence, Object>();

        for (Map.Entry<CharSequence, Object> charSequenceEntry : n)
            hashMap.put(charSequenceEntry.getKey(), charSequenceEntry.getValue());
        return hashMap;

    }

    /**
     * Returns an iterator over a set of elements of type T.
     *
     * @return an Iterator.
     */
    public Iterator<Map.Entry<CharSequence, Object>> iterator() {
        return this.entrySet().iterator();
    }
}