package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;

import java.util.*;
import java.util.logging.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public class ipcRouter implements Router {
    private Map<String, Map> paths = new WeakHashMap<String, Map>();

    public void remove(String key) {
        AbstractAgent n = (AbstractAgent) paths.get(key);
        n.handle_Dissolve(null);
        paths.remove(key);
    }


    public void send(Map<String, ?> message) {
//        if (Env.getInstance().logDebug)
//            Env.getInstance().log(500, getClass().getName() + " sending message to" + getDestination(message));
        MetaPropertiesFilter node = (MetaPropertiesFilter) paths.get(getDestination(message));
        node.recv(new Message(message));
    }

    public String getDestination(Map<String, ?> item) {
        return String.valueOf(item.get(Message.DESTINATION_KEY)); //
    }

    public boolean pathExists(Map<String, ?> message) {
        if (!paths.containsKey(getDestination(message)))
            return false;

        send(message);
        return true;
    }

    public Set getPool() {
        return paths.keySet(); //
    }


    public boolean hasPath(String key) {
        return paths.containsKey(key); //
    }

    public void put(AbstractAgent node) {
        paths.put(node.getJMSReplyTo(), node); //
    }

    public boolean addPath(Map agentProxy) {
        if (agentProxy instanceof Agent) {
            //check for a previous element of same name...
            // dissolve it..
            AbstractAgent n = (AbstractAgent) paths.get(Message.REPLYTO_KEY);
            if (n != null) {
                n.handle_Dissolve(null);
            }
            paths.put((String) agentProxy.get(Message.REPLYTO_KEY), agentProxy);

            Logger.getAnonymousLogger().finest(" adding agentProxy " + agentProxy.get(Message.REPLYTO_KEY));
            return true;
        }
        return false;
    }

}