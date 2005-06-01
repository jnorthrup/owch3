package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.kernel.Location;
import net.sourceforge.owch2.kernel.MetaProperties;
import net.sourceforge.owch2.kernel.Notification;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author James Northrup
 * @version $Id: httpRouter.java,v 1.1 2005/06/01 06:43:12 grrrrr Exp $
 */
public class httpRouter implements Router {
    static long ser = 0;
    private Map elements = new TreeMap();
    Socket p;

    public void remove(Object key) {
        elements.remove(key);
    }

    ;

    public Object getDestination(Map item) {
        return item.get("JMSDestination");
    }

    ;

    public Set getPool() {
        return elements.keySet();
    }

    ;

    public boolean hasElement(Object key) {
        return elements.containsKey(key);
    }

    ;

    public boolean addElement(Map item) {
        Location met = new Location();
        met.put("JMSReplyTo", item.get("JMSReplyTo").toString());
        met.put("URL", item.get("URL").toString());
        elements.put(item.get("JMSReplyTo"), met);
        return true;
    }

    ;

    public void send(Map item) {
        Notification n = new Notification(item);
        if (n.getJMSReplyTo() == null) {
            return;
        }
        Date d = new Date();
        String serr = n.get("JMSReplyTo") + ":" + n.get("JMSDestination").toString() + ":" + n.get("JMSType").toString() +
                "[" + d.toString() + "] " + ser++;
        n.put("URL", Env.getInstance().getLocation("http").getURL());
        MetaProperties prox = (MetaProperties) elements.get(n.get("JMSDestination"));
        if (prox == null) {
            prox = (MetaProperties) Env.getInstance().getParentNode();
        }
        String u = prox.get("URL").toString();
        //try {
        try {
            if (u == null) {
                if (Env.getInstance().isParentHost()) {
                    if (Env.getInstance().logDebug)
                        Env.getInstance().log(2, "******Domain:  DROPPING PACKET FOR " + prox.get("JMSReplyTo"));
                    return;
                } else {
                    u = Env.getInstance().getParentNode().getURL();
                }
            }
            URL url = new URL(u);
            Socket s = new Socket(url.getHost(), url.getPort());
            OutputStream os = s.getOutputStream();
            os.write("POST /owch\n".getBytes());
            n.save(os);
            s.close();
        }
        catch (IOException e) {
            n.remove(u);
            Env.getInstance().send(n);
        }
        /*}
        catch (Exception e) {

        }
        ; */
    }

    ;
}

;


