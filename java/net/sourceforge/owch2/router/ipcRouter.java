package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;

import java.util.*;
import java.util.logging.*;

/**
 * @author James Northrup
 * @version $Id: ipcRouter.java,v 1.1 2005/06/03 18:27:48 grrrrr Exp $
 */
public class ipcRouter implements Router {
    private Map elements = new WeakHashMap();

    public ipcRouter() {
    }

    public void remove(Object key) {
        AbstractAgent n = (AbstractAgent) elements.get(key);
        n.handle_Dissolve(null);
        elements.remove(key);
    }

    public void send(Map item) {
        if (Env.getInstance().logDebug)
            Logger.global.info(getClass().getName() + " sending item to" + getDestination(item));
        Agent node = (Agent) elements.get(getDestination(item));
        node.recv(new Notification(item));
    }

    public Object getDestination(Map item) {
        return item.get(Notification.DESTINATION_KEY);
    }

    public Set getPool() {
        return elements.keySet(); //
    }

    public boolean hasElement(Object key) {
        return elements.containsKey(key); //
    }

    public void put(AbstractAgent node) {
        elements.put(node.getJMSReplyTo(), node); //
    }

    public boolean proxyAccepted(Map item) {
        if (item instanceof Agent) {
            //check for a previous element of same name...
            // dissolve it..
            AbstractAgent n = (AbstractAgent) elements.get(Notification.REPLYTO_KEY);
            if (n != null) {
                n.handle_Dissolve(null);
            }
            elements.put(item.get(Notification.REPLYTO_KEY), item);
            Logger.global.info(" adding item " + item.get(Notification.REPLYTO_KEY));
            return true;
        }
        return false;
    }

}


