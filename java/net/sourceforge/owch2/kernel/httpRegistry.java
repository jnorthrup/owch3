package net.sourceforge.owch2.kernel;

import static net.sourceforge.owch2.kernel.ProtocolType.*;

import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * gatekeeper registers a prefix of an URL such as "/cgi-bin/foo.cgi" The algorithm to locate the URL works in 2 phases;
 * <OL>
 * <LI> The weakHashMap is checked for an exact match. <LI> The arraycache is then checked from top to bottom to see if
 * URL startswith (element <n>) </OL> The when an URL is located -- registering the URL "/" is a sure
 * bet, the owch agent registered in the WeakHashMap is notified of a waiting pipeline
 * <p/>
 * <p/>
 * update.... 12 years after i wrote this... java had NO REGEX back when this was written.  no jit either.
 * <p/>
 * I like the simple algorithm, but it's pretty obvious how to specialize this for a more regexy kind of registration.
 *
 * @author James Northrup
 * @version $Id: HttpRegistry.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class HttpRegistry {
    private NavigableMap<String, MetaAgent> registeredResources = new ConcurrentSkipListMap<String, MetaAgent>();
    public Map<String, Socket> httpdSockets = new ConcurrentHashMap<String, Socket>();

    public HttpRegistry() {
    }

    public void registerItem(String resource, Location l) {
        MetaAgent agent = registeredResources.put(resource, l);

    }

    public void unregisterItem(String item) {
        registeredResources.remove(item);
    }

    class URLComparator implements Comparator {
        /**
         * Compares its two arguments for order.  In this case order is defined by strlen and then by content sorting.
         */
        public int compare(Object o1, Object o2) {
            int res = o1.toString().length() - o2.toString().length();
            if (res == 0) {
                res = o1.toString().compareTo(o2.toString());
            } //equal length objects are then copmred as strings
            return res;
        }
    }


    /**
     * dispatchRequest
     *
     * @return whether the request was fulfilled from this agent host's registrant.
     */
    public boolean dispatchRequest(Socket socket, MetaProperties notification) {
        String resource = notification.get("Resource").toString();
        MetaAgent registrant = getResourceHandler(resource);

        String method = notification.get("Method").toString();
        notification.put("Proxy-Request", notification.get("Request"));

        if (registrant != null) {
            String lname = registrant.getJMSReplyTo();

            if (ipc.routerInstance().hasPath(lname)) {
                httpdSockets.put(socket.toString(), socket);
                notification.put("_Socket", socket.toString());
                notification.put("JMSDestination", lname);
                notification.put("JMSType", "httpd");
                notification.put("JMSReplyTo", "nobody"); //apparently we *MUST* give ourselves a name..
                Env.getInstance().send(notification);
                return true;
            }
            PipeSocket pipeSocket = new httpPipeSocket(socket, registrant, notification);
            return true;
        }
        return false;
    }


    /**
     * @param resource a requested url
     * @return returns the resource, or the nearest matching parent of that resource, or the shortest resource of all.
     */
    private MetaAgent getResourceHandler(String resource) {

        if (registeredResources.containsKey(resource)) return registeredResources.get(resource);
//        MetaAgent registrant = registeredResources.get(resource);


        NavigableMap<String, MetaAgent> map = registeredResources.headMap(resource, false);
        NavigableSet<String> navigableSet = map.descendingKeySet();

        String key = null;
        for (String s : navigableSet) {
            key = s;
            if (resource.startsWith(s)) {
                break;
            }
        }
        return registeredResources.get(key);
    }

}


