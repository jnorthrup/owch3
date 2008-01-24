package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.kernel.Location;
import net.sourceforge.owch2.kernel.MetaProperties;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author James Northrup
 * @version $Id: DomainRouter.java,v 1.1 2005/06/01 06:43:12 grrrrr Exp $
 */
public class DomainRouter implements Router {
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

    public boolean addElement(Map item) {
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


