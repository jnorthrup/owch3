package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.AbstractAgent;
import net.sourceforge.owch2.kernel.Agent;
import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.kernel.Notification;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author James Northrup
 * @version $Id: IPCRouter.java,v 1.1 2005/06/01 06:43:12 grrrrr Exp $
 */
public class IPCRouter implements  Router {
    private Map elements = new WeakHashMap();

    public void remove(Object key) {
        AbstractAgent n = (AbstractAgent) elements.get(key);
        n.handle_Dissolve(null);
        elements.remove(key);
    }

    ;

    public void send(Map item) {
        if (Env.getInstance().logDebug)
            Env.getInstance().log(500, getClass().getName() + " sending item to" + getDestination(item));
        Agent node = (Agent) elements.get(getDestination(item));
        node.recv(new Notification(item));
    }

    public Object getDestination(Map item) {
        return item.get("JMSDestination"); //
    }

    ;

    public Set getPool() {
        return elements.keySet(); //
    }

    ;

    public boolean hasElement(Object key) {
        return elements.containsKey(key); //
    }

    ;

    public void put(AbstractAgent node) {
        elements.put(node.getJMSReplyTo(), node); //
    }

    public boolean addElement(Map item) {
        if (item instanceof Agent) {
            //check for a previous element of same name...
            // dissolve it..
            AbstractAgent n = (AbstractAgent) elements.get("JMSReplyTo");
            if (n != null) {
                n.handle_Dissolve(null);
            }
            elements.put(item.get("JMSReplyTo"), item);
            if (Env.getInstance().logDebug)
                Env.getInstance().log(500, getClass().getName() + " adding item " + item.get("JMSReplyTo"));
            return true;
        }
        return false;
    }

    ;
}

;


