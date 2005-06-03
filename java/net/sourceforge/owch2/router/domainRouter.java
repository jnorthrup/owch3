package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;

import java.util.*;

/**
 * @author James Northrup
 * @version $Id: domainRouter.java,v 1.1 2005/06/03 18:27:47 grrrrr Exp $
 */
public class domainRouter implements Router {
    private Map elements = new TreeMap();

    public void remove(Object key) {
        elements.remove(key);
    }

    ;

    public Object getDestination(Map item) {
        return item.get("Domain-Gateway");
    }

    ;

    public Set getPool() {
        return null;
    }

    ;

    public boolean proxyAccepted(Map item) {
        if (item.containsKey("Domain-Gateway")) {
            try {
                MetaProperties mp = new Location();
                mp.put("JMSReplyTo", item.get("JMSReplyTo").toString()); //looks
                // like joe@joedomain
                mp.put("Domain-Gateway", item.get("Domain-Gateway").toString()); //looks
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

    ;

    public void send(Map item) {
        Map domain = (Map) elements.get(item.get("JMSDestination")); //looks
        // like joe@joedomain
        Object dest = domain.get("JMSReplyTo"); //looks like "bob"
        item.put("JMSReplyTo", item.get("JMSReplyTo") + "@" + Env.getInstance().getDomainName()); //looks
        // like bob@bobdomain
        item.put("JMSDestination", dest); //looks like joe
        item.put("Domain-Gateway", Env.getInstance().getDomainName()); //looks like bobdomain
    }

    ;

    public boolean hasElement(Object key) {
        return elements.containsKey(key);
    }

    ;
}

;


