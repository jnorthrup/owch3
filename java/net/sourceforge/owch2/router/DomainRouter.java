package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;
import static net.sourceforge.owch2.kernel.Message.*;
import net.sourceforge.owch2.agent.*;
import static net.sourceforge.owch2.agent.Domain.*;

import java.util.*;

/**
 * @author James Northrup
 * @version $Id: domainRouter.java,v 1.1 2005/06/01 06:43:12 grrrrr Exp $
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
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Set getPool() {
        return null;
    }

    public boolean addElement(Map item) {
        if (item.containsKey(DOMAIN_GATEWAY_KEY)) {
            try {
                MetaProperties mp = new Location();
                mp.put("JMSReplyTo", item.get("JMSReplyTo").toString()); //looks
                // like joe@joedomain
                mp.put(DOMAIN_GATEWAY_KEY, item.get(DOMAIN_GATEWAY_KEY).toString()); //looks
                // like "joedomain"
                elements.put(item.get("JMSReplyTo"), mp);
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
     * This adds domain context to the REPLYTO before sending a message to a new domain with an opaque address and a
     * @param message
     */
    public void send(Map message) {
        Map domain = (Map) elements.get(message.get("JMSDestination")); //looks
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


