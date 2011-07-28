package net.sourceforge.owch2.kernel;

import net.sourceforge.owch2.router.*;

import java.lang.reflect.*;
import java.util.*;

/**   AbstractAgent provides the base class which communicates with the Env
 * agent host platform and the protocols it operates. communication is
 * handled by constructing a MetaProperties Object and calling the
 * send() method of the AbstractAgent.  The Env Host platform manages the details of protocols, routing, and delivery to
 * other nodes in the namespace.
 * @version $Id: AbstractAgent.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
abstract public class AbstractAgent extends TreeMap implements Agent {
    protected boolean killFlag = false;
    boolean virgin;
    LinkRegistry acl = null;

    private final static Class[] cls_m = new Class[]{MetaProperties.class};

    private static final Class[] no_class = new Class[0];
    private static final Object[] no_Parm = new Object[0];
    private static final String default_val = "default".intern();

    /**
     * Gets one of this object's properties using the associated key.
     * @see #putValue
     */
    public Object getValue(String key) {
        if (Env.logDebug) Env.log(499, getClass().getName() + ":" + key);
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
        return value;
    }

    /**
     * Sets one of this object's properties using the associated key. If the value has
     * changed, a <code>PropertyChangeEvent</code> is sent to listeners.
     * @param key    a <code>String</code> containing the key
     * @param value  an <code>Object</code> value
     */
    public void putValue(String key, Object value) {
        String attempt = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
        if (Env.logDebug)
            Env.log(499,
                    getClass().getName() + ":" + key + "=" + value + "::" + value.getClass().getName());
        Class c = this.getClass();
        Class[] vclass = new Class[]{value.getClass()};
        try {
            Method m = c.getMethod(attempt, vclass);
            m.invoke(this,
                    new Object[]{value});
        }
        catch (NoSuchMethodException nsm) {
            if (Env.logDebug) Env.log(499, attempt);
          nsm.printStackTrace();
            try {
                Field f = c.getField(key);
                f.set(this, value);
            }
            catch (Exception e) {
                this.put(key, value);
            }
        }
        catch (Exception e) {
            this.put(key, value);
        }
        finally {
        }
    }


    public boolean isParent() {
        return false; //
    }

  /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     * @param lk node(s) to link to
     */
    public void linkTo(String lk) {
        if (lk == null) {
            if (Env.logDebug) Env.log(5, getClass().getName() + "::" + this.getJMSReplyTo() + ".link invoked. routing to default");
            lk = Env.getParentNode().getJMSReplyTo();
        }
      MetaProperties n = new Notification();
        n.put("JMSDestination", lk);
        n.put("JMSType", "Link");
        send(n);
    }

    /**
     * AbstractAgent level Notification insignia creation and inter-process notification routing.
     * @param n  Notification destined for somewhere else
     */
    public void send(MetaProperties n) {
        String d = null,w = null;
        Agent node = null;
        //see if this works out
        if (n.getJMSReplyTo() == null) {
            n.put("JMSReplyTo", this.getJMSReplyTo());
        }
      d = (String) n.get("JMSDestination");
        if (d == null) {
            if (Env.logDebug) Env.log(8, "debug: AbstractAgent.Send(Notification) dropping unsendd Notification from " + getJMSReplyTo());
            return;
        }
      Env.send(n);
    }

  public final void recv(MetaProperties notificationIn) {
        String JMSType = (String) notificationIn.get("JMSType");
        try {
            getClass().getMethod("handle_" + JMSType, cls_m).invoke(this,
                notificationIn);
        }
        catch (InvocationTargetException e) {
            if (Env.logDebug)
                Env.log(2, "" + e.getTargetException() + " thrown within " + this.getJMSReplyTo() + "::" +
                        getClass().getName() + "->" + JMSType);
            e.getTargetException().printStackTrace();
        }
        catch (Exception e) {
            if (Env.logDebug) Env.log(2, "" + e + " thrown for " + this.getJMSReplyTo() + "::" + getClass().getName() + "->" + JMSType);
        }
  }

    public AbstractAgent() {
    }

    public AbstractAgent(Map proto) {
        super(proto);
        Env.getRouter("IPC").addElement(this);
        if (!isParent()) {
            linkTo("default");
        }
    }

    public void init(Map proto) {
        putAll(proto);
        Env.getRouter("IPC").addElement(this);
        if (!isParent()) {
            linkTo("default");
        }
    }

    /** this tells our (potentially clone) agent to stop re-registering.  it will cease to spin. */
    public void handle_Dissolve(MetaProperties n) {
        killFlag = true;
        //UnLink("default");
        Router r[] = {
            Env.getRouter("IPC"),
            Env.getRouter("owch"),
            Env.getRouter("http"),
        };
        for (int i = 0; i < r.length; i++) {
            try {
                r[i].remove(this);
            }
            catch (Exception e) {
            }
        }
    }

    public void handle_Link(MetaProperties p) {
        String dest = p.getJMSReplyTo();
        MetaProperties n = new Notification();
        n.put("JMSType", "Update");
        n.put("JMSDestination", dest);
        send(n);
        if (Env.logDebug) Env.log(15, getClass().getName() + "::" + getJMSReplyTo() + " AbstractAgent.update() sent for " + dest);
    }


  /**
     * Sends an update to another AbstractAgent
     *  @param dest JMSReplyTo
     */
    public void handle_Update(MetaProperties p) {
        String dest = p.getJMSReplyTo();
        MetaProperties n = new Notification();
        n.put("JMSType", "Updated");
        n.put("JMSDestination", dest);
        send(n);
        if (Env.logDebug) Env.log(15, getClass().getName() + "::" + getJMSReplyTo() + " AbstractAgent.update() sent for " + dest);
    }

    /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     * @param lk node(s) to link to
     */
    public void handle_Unlink(MetaProperties m) {
        String lk = m.getJMSReplyTo();
        if (lk == null) {
            if (Env.logDebug) Env.log(5, getClass().getName() + "::" + this.getJMSReplyTo() + ".unlink invoked. routing to default");
            lk = Env.getParentNode().getJMSReplyTo();
        }
      MetaProperties n = new Notification();
        n.put("JMSDestination", lk);
        n.put("JMSType", "UnLink");
        send(n);
    }

    public final String getURL() {
        String s = (String) get("URL");
        return s;
    }

    public final String getJMSReplyTo() {
        return (String) get("JMSReplyTo");
    }

  /**  move - clone self.<OL><LI> node arrives at new host,
     *    registers in Nodecache on opaque name. <LI>sends
     *    "Dissolve" message and registers original
     *    name.  <LI>Dissolver registers new opaque name,
     *    informs new clone to register.<LI> awaits
     *    dissolve.
     * @param notificationIn the payload describing the move
     */

    public void handle_Move(MetaProperties notificationIn) {

        String host = notificationIn.get("Host").toString(); //name of a Deploy agent
        if (host == null) {
            host = Env.getHostname();
        }
        // move_state1(host);
    }

    public void clone_state1(String host) {
        MetaProperties n2 = Env.getLocation("http");
        n2.putAll(this);
        n2.put("JMSType", "DeployNode");
        n2.put("Class", getClass().getName());
        n2.put("JMSReplyTo", getJMSReplyTo());
        n2.put("Source", Env.getLocation("http").getURL() + get("Resource"));
        //resource remains constant in this incarnation
        //n2.put( "Resource",get("Resource"));//produces 3 Strings
        n2.put("JMSDestination", host);
        send(n2);
    }

  /**
     clone <OL><LI>recv order to clone, and host<LI>  deploy
     new class.  <LI>deliver content.  <LI>close channel.
     */

    public void handle_Clone(MetaProperties n) {

        String host = n.get("Host").toString(); //name of a Deploy agent
        if (host == null) {
            host = Env.getHostname();
        }
        clone_state1(host);
    }

    /**
     * intended for automatic maintenance.. so to speak, for survival
     * tasks.. period replication, metrics evalutation, etc.  lots o TBD in this method
     */
    public void relocate() {
        if (containsKey("Clone")) {
            String clist = (String) get("Clone");
            remove("Clone");
            if (Env.logDebug) Env.log(500, getClass().getName() + " **Cloning for " + clist);
            StringTokenizer st = new StringTokenizer(clist);
            while (st.hasMoreTokens()) {
                clone_state1(st.nextToken());
            }
        }
      if (containsKey("Deploy")) {
            try {
                String clist = (String) get("Deploy");
                remove("Deploy");
                if (Env.logDebug) Env.log(500, getClass().getName() + " **Cloning for " + clist);
                StringTokenizer st = new StringTokenizer(clist);
                while (st.hasMoreTokens()) {
                    clone_state1(st.nextToken());
                }
                Thread.currentThread().sleep(15 * 1000); //kludge,
                // allow udp messages to arrive...
                System.exit(0); //TODO: allow our host
                // to stay alive...
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


