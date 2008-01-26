package net.sourceforge.owch2.kernel;

import static net.sourceforge.owch2.kernel.AgentLifecycle.*;
import static net.sourceforge.owch2.kernel.Message.*;
import net.sourceforge.owch2.router.*;

import static java.lang.Thread.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

/**
 * AbstractAgent provides the base class which communicates with the Env
 * agent host platform and the protocols it operates. communication is
 * handled by constructing a MetaProperties Object and calling the
 * send() method of the AbstractAgent.  The Env Host platform manages the details of protocols, routing, and delivery to
 * other nodes in the namespace.
 *
 * @author James Northrup
 * @version $Id: AbstractAgent.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public abstract class AbstractAgent<V> extends TreeMap<String, V> implements Agent {
    protected static final String MOBILEHOST_KEY = "Host";
    protected static final String DEFAULT_LINK_NAME = "default";

    protected static final String DEPLOYNODE_TYPE = DeployNode.toString();
    protected static final String UNLINK_TYPE = UnLink.toString();
    protected static final String CLONE_KEY = Clone.toString();
    protected static final String UPDATED_TYPE = Updated.toString();
    protected static final String UPDATE_TYPE = Update.toString();
    protected static final String LINK_TYPE = Link.toString();

    protected boolean killFlag = false;
    boolean virgin;
//    LinkRegistry acl = null;

    private final static Class[] cls_m = new Class[]{MetaProperties.class};

    private static final Class[] no_class = new Class[0];
    private static final Object[] no_Parm = new Object[0];
    protected static final String CLASSTYPE_KEY = "Class";

    /**
     * Gets one of this object's properties using the associated key.
     *
     * @see #putValue
     */
    public V getValue(String key) {
//        if (Env.logDebug) Env.log(499, getClass().getName() + ":" + key);
        Object value = null; //= default_val;
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
        return (V) value;
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
                this.put(key, (V) value);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //!TODO: review for fit
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //!TODO: review for fit
        }
    }


    public boolean isParent() {
        return false; //
    }

    /**
     * Sends a Link notification other node(s) intended to
     * establish direct socket communication.
     *
     * @param linkDestination node(s) to link to
     */
    public void linkTo(String linkDestination) {
        if (linkDestination == null) {
            Logger.getAnonymousLogger().info(".link invoked. routing to default");
            linkDestination = Env.getInstance().getParentNode().getJMSReplyTo();
        }
        MetaProperties n = new Message();
        n.put(DESTINATION_KEY, linkDestination);
        n.put(TYPE_KEY, LINK_TYPE);
        send(n);
    }

    /**
     * AbstractAgent level Message insignia creation and inter-process notification routing.
     *
     * @param n Message destined for somewhere else
     */
    public void send(MetaProperties n) {


        if (n.getJMSReplyTo() == null) {
            n.put(REPLYTO_KEY, this.getJMSReplyTo());
        }
        if (n.get(DESTINATION_KEY) != null) {
            Env.getInstance().send(n);
        }
    }


    public final void recv(MetaProperties notificationIn) {
        try {

            String msgType = (String) notificationIn.get(TYPE_KEY);
            //for later optimizations.... switch(  valueOf(msgType)) ... is enabled.

            getClass().getMethod("handle_" + msgType, cls_m).invoke(this,
                    new Object[]{notificationIn});

        }
        catch (InvocationTargetException e) {
            e.getTargetException().printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public AbstractAgent() {
    }

    public AbstractAgent(Map proto) {
        super(proto);
        ProtocolType.ipc.routerInstance().pathExists(this);
        if (!isParent()) {
            linkTo(DEFAULT_LINK_NAME);
        }
    }

    public void init(Map<String, ? extends V> proto) {
        putAll(proto);
        ProtocolType.ipc.routerInstance().pathExists(this);
        if (!isParent()) {
            linkTo(DEFAULT_LINK_NAME);
        }
    }

    /**
     * this tells our (potentially clone) agent to stop re-registering.
     * it will cease to spin.
     */
    public void handle_Dissolve(MetaProperties n) {
        killFlag = true;
        //UnLink("default");
        Router r[] = {
                ProtocolType.ipc.routerInstance(),
                ProtocolType.owch.routerInstance(),
                ProtocolType.Http.routerInstance(),
        };
        for (int i = 0; i < r.length; i++) {
            try {
                r[i].remove(getJMSReplyTo());
            }
            catch (Exception e) {
            }
        }
    }

    public void handle_Link(MetaProperties p) {
        String dest = p.getJMSReplyTo();
        MetaProperties n = new Message();
        n.put(TYPE_KEY, UPDATE_TYPE);
        n.put(DESTINATION_KEY, dest);
        send(n);
        Logger.getAnonymousLogger().info(getClass().getName() + "::" + getJMSReplyTo() + " AbstractAgent.update() sent for " + dest);
    }


    /**
     * Sends an update to another AbstractAgent
     *
     * @param p JMSReplyTo
     */
    public void handle_Update(MetaProperties p) {
        String dest = p.getJMSReplyTo();
        MetaProperties n = new Message();
        n.put(TYPE_KEY, UPDATED_TYPE);
        n.put(DESTINATION_KEY, dest);
        send(n);
        Logger.getAnonymousLogger().info(getClass().getName() + "::" + getJMSReplyTo() + " AbstractAgent.update() sent for " + dest);
    }

    /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     *
     * @param m node(s) to link to
     */
    public void handle_Unlink(MetaProperties m) {
        String lk = m.getJMSReplyTo();
        if (lk == null) {

            lk = Env.getInstance().getParentNode().getJMSReplyTo();
        }

        MetaProperties n = new Message();
        n.put(DESTINATION_KEY, lk);
        n.put(TYPE_KEY, UNLINK_TYPE);
        send(n);
    }

    public final String getURI() {
        return (String) get(URI_KEY);

    }

    public final String getJMSReplyTo() {
        return (String) get(REPLYTO_KEY);
    }

    /**
     * move - clone self.<OL><LI> node arrives at new host,
     * registers in Nodecache on opaque name. <LI>sends
     * "Dissolve" message and registers original
     * name.  <LI>Dissolver registers new opaque name,
     * informs new clone to register.<LI> awaits
     * dissolve.
     *
     * @param notificationIn the payload describing the move
     */
    public void handle_Move(MetaProperties<? extends String> notificationIn) {

        String host = notificationIn.get(MOBILEHOST_KEY); //name of a Deploy agent
        if (host == null) {
            host = Env.getInstance().getHostname();
        }
        //if (Env.logDebug) Env.log(50, "Env.getLocation - " + ProtocolType);

        MetaProperties response = (MetaProperties) ProtocolType.Http.getLocation().clone();
        response.putAll(this);
        response.put(TYPE_KEY, DEPLOYNODE_TYPE);
        response.put(CLASSTYPE_KEY, getClass().getName());
        response.put(REPLYTO_KEY, getJMSReplyTo());

        response.put(SOURCE_KEY, ProtocolType.Http.getLocation().getURI() + get(RESOURCE_KEY));

        response.put(DESTINATION_KEY, host);
        send(response);
        killFlag = true;

    }

    public void clone_state1(String host) {

        MetaProperties response = (MetaProperties) ProtocolType.Http.getLocation().clone();
        response.putAll(this);
        response.put(TYPE_KEY, DEPLOYNODE_TYPE);
        response.put(CLASSTYPE_KEY, getClass().getName());
        response.put(REPLYTO_KEY, getJMSReplyTo());
        //if (Env.logDebug) Env.log(50, "Env.getLocation - " + ProtocolType);

        response.put(SOURCE_KEY, ProtocolType.Http.getLocation().getURI() + get(RESOURCE_KEY));
        //resource remains constant in this incarnation
        //n2.put( "Resource",get("Resource"));//produces 3 Strings
        response.put(DESTINATION_KEY, host);
        send(response);
    }


    /**
     * clone <OL><LI>recv order to clone, and host<LI>  deploy
     * new class.  <LI>deliver content.  <LI>close channel.
     */
    public void handle_Clone(MetaProperties<? extends V> n) {

        String host = n.get(MOBILEHOST_KEY).toString(); //name of a Deploy agent
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
}


