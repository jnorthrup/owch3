package owch;

import java.net.*;
import java.io.*;
import java.util.*;

/*
  http://www.iconcomp.com/papers/comp/comp_41.html
*/

/**
$Id: Node.java,v 1.1.1.1.2.1 2001/04/30 04:27:56 grrrrr Exp $
 */
abstract public class Node extends TreeMap implements MetaNode {
    protected boolean killFlag = false;
    boolean virgin;
    LinkRegistry acl = null;

    /** this tells our (potentially clone) web page to stop re-registering.  it will cease to spin. */
    public void dissolve() {
        killFlag = true;
	//UnLink("default")
    };

    public boolean isParent() {
        return false;
    };

    public void receive(MetaProperties n) {
        String JMSType = (String)n.get("JMSType");
        if (JMSType.equals("Link")) {
            String name = n.getJMSReplyTo();
            update(name);
            return;
        };
        if ( JMSType.equals("Dissolve")) {
            dissolve();
            return;
        }
    }

    /**
     *  Sends an update to another Node
     *  @param dest JMSReplyTo
     */
    public void update(String dest) {
        MetaProperties n = new Notification();
        n.put("JMSType", "Update");
        n.put("JMSDestination", dest);
        send(n);
        Env.debug(15, getClass().getName() + "::" + getJMSReplyTo() + " Node.update() sent for " + dest);
    }

    public Node() {
    }

    public Node(Map p) {
        super(p);
        Env.getRouter("IPC").addElement(this);
        if (!isParent())
            linkTo("default");
    }

    public void init(Map p) {
        putAll(p);
        Env.getRouter("IPC").addElement(this);
        if (!isParent())
            linkTo("default");
    }

    /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     * @param lk node(s) to link to
     */
    public void linkTo(String lk) {
        if (lk == null) {
            Env.debug(5, getClass().getName() + "::" + this.getJMSReplyTo() + ".link invoked. routing to default");
            lk = Env.getParentNode().getJMSReplyTo();
        };
        MetaProperties n = new Notification();
        n.put("JMSDestination", lk);
        n.put("JMSType", "Link");
        send(n);
    }

    /**
     * Sends a Link notification other node(s) intended to establish direct socket communication.
     * @param lk node(s) to link to
     */
    public void unlink(String lk) {
        if (lk == null) {
            Env.debug(5, getClass().getName() + "::" + this.getJMSReplyTo() + ".unlink invoked. routing to default");
            lk = Env.getParentNode().getJMSReplyTo();
        };
        MetaProperties n = new Notification();
        n.put("JMSDestination", lk);
        n.put("JMSType", "UnLink");
        send(n);
    }

    /**
     * Node level Notification insignia creation and inter-process notification routing.
     * @param n  Notification destined for somewhere else
     */
    public void send(MetaProperties n) {
        String d = null;
        String w = null;
        Node node = null;
        //see if this works out
        if (n.getJMSReplyTo() == null) {
            n.put("JMSReplyTo", this.getJMSReplyTo());
        };
        d = (String)n.get("JMSDestination");
        if (d == null) {
            Env.debug(8, "debug: Node.Send(Notification) dropping unsendd Notification from " + getJMSReplyTo());
            return;
        };
        Env.send(n);
    };

    public final String getURL() {
        String s = (String)get("URL");
        return s;
    }

    public final String getJMSReplyTo() {
        return (String)get("JMSReplyTo");
    };
};

//$Log: Node.java,v $
//Revision 1.1.1.1.2.1  2001/04/30 04:27:56  grrrrr
//SocketProxy + Deploy methods
//
