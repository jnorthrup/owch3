package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * this is an alternate to an owch router.   needs testing.
 *
 * @author James Northrup
 * @version $Id: httpRouter.java,v 1.2 2005/06/03 18:27:48 grrrrr Exp $
 */
public class httpRouter implements Router {
    static long ser = 0;
    private Map<String, MetaAgent> elements = new TreeMap<String, MetaAgent>();
    Socket p;

    public void remove(Object key) {
        elements.remove(key);
    }


    public Object getDestination(Map item) {
        return item.get(Notification.DESTINATION_KEY);
    }


    public Set getPool() {
        return elements.keySet();
    }


    public boolean hasElement(Object key) {
        return elements.containsKey(key);
    }


    public boolean proxyAccepted(Map item) {
        Location met = new Location();

        met.put(Notification.REPLYTO_KEY, item.get(Notification.REPLYTO_KEY).toString());
        met.put(Notification.URI_KEY, item.get(Notification.URI_KEY).toString());
        elements.put(item.get(Notification.REPLYTO_KEY).toString(), met);

        return true;
    }


    public void send(Map item) {
        Notification n = new Notification(item);
        if (n.getJMSReplyTo() == null) {
            return;
        }
//        String serr = n.get(Notification.REPLYTO_KEY) + ":" + n.get("JMSDestination").toString() + ":" + n.get("JMSType").toString() +                "[" + d.toString() + "] " + ser++;

        MetaProperties location = ProtocolType.Http.getLocation();
        n.put(Notification.URI_KEY, location.getURI());
        MetaProperties prox = (MetaProperties) elements.get(n.get(Notification.DESTINATION_KEY));
        if (prox == null) {
            prox = (MetaProperties) Env.getInstance().getParentNode();
        }
        URI uri = null;
        try {
            uri = URI.create(prox.get(Notification.URI_KEY).toString());
        } catch (Exception e) { //URL parse issues.
        }

        try {
            if (uri == null) {
                if (Env.getInstance().isParentHost()) {
                    if (Env.getInstance().logDebug)
                        Logger.global.info("******Domain:  DROPPING PACKET FOR " + prox.get(Notification.REPLYTO_KEY));
                    return;
                } else {
                    uri = Env.getInstance().getParentNode().getURI();
                }
            }

            Socket socket = new Socket(uri.getHost(), uri.getPort());
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("POST /owch\n".getBytes());
            n.save(outputStream);
            socket.close();
        }
        catch (IOException e) {
            n.remove(uri);
            Env.getInstance().send(n);
        }
    }
}


