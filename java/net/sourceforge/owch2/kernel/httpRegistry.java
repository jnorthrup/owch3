package net.sourceforge.owch2.kernel;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.Socket;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.WeakHashMap;

/**
 * gatekeeper registers a prefix of an URL such as "/cgi-bin/foo.cgi" The algorithm to locate the URL works in 2 phases;<OL>
 * <LI> The weakHashMap is checked for an exact match. <LI> The arraycache is then checked from top to bottom to see if
 * URL startswith (element <n>) </OL> The when an URL is located -- registering the URL "/" is a sure
 * bet, the owch agent registered in the WeakHashMap is notified of a waiting pipeline
 *
 * @author James Northrup
 * @version $Id: httpRegistry.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class httpRegistry extends Registry {
    public httpRegistry() {
        int a = 4;

        /** references URL prefix-> NodeName */
        setWeakMap(new WeakHashMap(384));
        setComparator(new URLComparator());
        setSet(new TreeSet(getComparator()));
    }

    public String displayKey(Comparable key) {
        return key.toString();
    }

    ;

    public String displayValue(Reference reference) {
        Map map = (Map) reference.get();
        if (map == null) {
            return "*something utterly unimportant*";
        }

        return map.get("JMSReplyTo").toString();
    }

    ;

    /**
     * references key ->content
     */
    public Reference referenceValue(Object o) {
        return new SoftReference(o, getRefQ());
    }

    ;

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

        ;

        /**
         * Indicates whether some other object is "equal to" this Comparator.
         */
        public boolean equals(Object obj) {
            return true;
        }

        ;
    }

    ;
    //holds the reference
    // to url strings


    /**
     * dispatchRequest
     *
     * @return whether the request was fulfilled yet.
     */
    public boolean dispatchRequest(Socket socket, MetaProperties notification) {
        String resource = notification.get("Resource").toString();
        String method = notification.get("Method").toString();
        notification.put("Proxy-Request", notification.get("Request"));
        MetaAgent l;
        //it is important to recache before we look up any weak keys
        //from URLNodeMap since cacheArray is holding registrations alive.
        if (isCacheInvalid()) {
            reCache();
        }
        //1 check resource for an exact match in our WeakRefMap
        l = (MetaAgent) weakGet(resource);
        if (l == null) {
            int len = resource.length();
            for (int i = getCache().length - 1; i >= 0; i--) {
                String temp = getCache(i).toString();
//                if (Env.logDebug) Env.log(500, "Pattern test on " + resource + ":" + temp);
                if (temp.length() > len) {
                    continue;
                }
                if (resource.startsWith(temp)) {
                    l = (MetaAgent) weakGet(temp);
//                    if (Env.logDebug) Env.log(500, "Pattern match on " + resource + ":" + temp);
                }
            }
        }
        if (l != null) {
            String lname = l.getJMSReplyTo();
            //check to see if the AbstractAgent that registered this
            // resource is actually present
            if (Env.getInstance().getRouter("IPC").hasElement(lname)) {
                //yes?  experimental...  just dump the
                //inbound Socket right into a
                //Notification... since we're certain
                //a node exists by this name
                notification.put("_Socket", socket);
                notification.put("JMSDestination", lname);
                notification.put("JMSType", "httpd");
                notification.put("JMSReplyTo", "nobody"); //apparently we
                // *MUST* give ourselves a name..
                Env.getInstance().send(notification);
                return true;
            }
            //3 create PipeConnection to registered location
//            if (Env.logDebug) Env.log(15, getClass().getName() + " creating PipeSocket for " + notification.get("Resource").toString());
            PipeSocket pipeSocket = new httpPipeSocket(socket, l, notification);
            return true;
        }
        ;
        //4 else super.sendFile
        return false;
    }
}

;


