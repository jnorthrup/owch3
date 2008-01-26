package net.sourceforge.owch2.router;

import static net.sourceforge.owch2.agent.Domain.*;
import net.sourceforge.owch2.kernel.*;
import static net.sourceforge.owch2.kernel.Message.*;

import java.util.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public class domainRouter implements Router {
    private Map elements = new TreeMap();

    public void remove(String key) {
        elements.remove(key);
    }

    public String getDestination(Map<String, ?> item) {
        return String.valueOf(item.get(DOMAIN_GATEWAY_KEY));
    }

    public boolean pathExists(Map<String, ?> item) {
        if (!item.containsKey(DOMAIN_GATEWAY_KEY))
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        addPath(item);
        return true;
    }

    public Set getPool() {
        return null;
    }

    public boolean addPath(Map item) {
        if (item.containsKey(DOMAIN_GATEWAY_KEY)) {
            try {
                MetaProperties mp = new Location();
                mp.put(Message.REPLYTO_KEY, item.get(Message.REPLYTO_KEY)); //looks
                // like joe@joedomain
                mp.put(DOMAIN_GATEWAY_KEY, item.get(DOMAIN_GATEWAY_KEY)); //looks
                // like "joedomain"
                elements.put(item.get(Message.REPLYTO_KEY), mp);
                return true;
            }
            catch (Exception e) //null ptr exceptions are hoped for..
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * This adds domain context to the REPLYTO before sending a message to a new
     * domain with an opaque address
     *
     * @param message msg
     */
    public void send(Map message) {
        Map domain = (Map) elements.get(message.get(Message.DESTINATION_KEY)); //looks
        // like joe@joedomain

        String dest = (String) domain.get(Message.REPLYTO_KEY); //looks like "bob"

        StringBuilder builder = new StringBuilder();
        builder.append(message.get(Message.REPLYTO_KEY));
        builder.append("@");
        builder.append(Env.getInstance().getDomainName());

        message.put(REPLYTO_KEY, builder.toString()); //looks
        // like bob@bobdomain
        message.put(DESTINATION_KEY, dest); //looks like joe
        message.put(DOMAIN_GATEWAY_KEY, Env.getInstance().getDomainName()); //looks like bobdomain
    }

    public boolean hasPath(String key) {
        return elements.containsKey(key);
    }
}