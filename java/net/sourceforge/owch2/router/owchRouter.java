package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;
import static net.sourceforge.owch2.kernel.Location.URI_KEY;
import static net.sourceforge.owch2.kernel.Notification.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * @author James Northrup
 * @version $Id: owchRouter.java,v 1.2 2005/06/03 18:27:48 grrrrr Exp $
 */
public class owchRouter implements Router {
    static long ser = 0;
    private Map<String, MetaAgent> proxies = new TreeMap<String, MetaAgent>();

    public void remove(Object key) {
        proxies.remove(key);
    }


    public Object getDestination(Map item) {
        return item.get(DESTINATION_KEY);
    }

    public Set getPool() {
        return proxies.keySet();
    }


    public boolean hasElement(Object key) {
        return proxies.containsKey(key);
    }


    public boolean proxyAccepted(Map item) {
        Location location = new Location();
        decorateProxy(location, item);
        return true;
    }

    private void decorateProxy(Location location, Map item) {
        location.put(REPLYTO_KEY, item.get(REPLYTO_KEY).toString());
        try {
            location.put(URI_KEY, item.get(URI_KEY).toString());
        }
        catch (Exception e) {
        }
        proxies.put(item.get(REPLYTO_KEY).toString(), location);
    }

    public void send(Map item) {
        Notification notification = new Notification(item);
        if (notification.getJMSReplyTo() == null) return;

        final String serial = createSerialNumber(notification);
        MetaProperties outProx = PrepareDelivery(notification, serial);
        URI destinationURI = null;
        if (outProx.containsKey(URI_KEY)) {
            destinationURI = URI.create(String.valueOf(outProx.get(URI_KEY)));
        } else {
            if (!Env.getInstance().isParentHost()) {
                destinationURI = Env.getInstance().getParentNode().getURI();
            } else {
                Logger.global.info("******Domain:  DROPPING PACKET FOR " + outProx.get(REPLYTO_KEY));
                return;
            }
        }


        try {
            byte[] buf;
            buf = createByteBuffer(notification);
            DatagramPacket p =
                    new DatagramPacket(buf, buf.length, InetAddress.getByName(
                            destinationURI.getHost()),
                            destinationURI.getPort());
            owchDispatch.getInstance().handleDatagram(serial, p, notification.get(PRIORITY_KEY) != null);
        }
        catch (UnknownHostException e) {
        }
        catch (IOException e) {
        }
    }

    private MetaProperties PrepareDelivery(Notification n, final String serial) {
        n.put(SERIALNUMBER_KEY, serial);

        MetaProperties l = ProtocolType.owch.getLocation();
        n.put(URI_KEY, l.getURI());
        MetaProperties outProx = getProxy(n);
        return outProx;
    }

    public byte[] createByteBuffer(Notification n) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        n.save(os);
        byte[] buf = os.toString().getBytes();
        return buf;
    }

    private MetaProperties getProxy(Notification n) {
        MetaProperties prox = (MetaProperties) proxies.get(n.get(DESTINATION_KEY));
        if (prox != null) {
            return prox;
        }
        prox = (MetaProperties) Env.getInstance().getParentNode();
        return prox;
    }

    private String createSerialNumber(Notification n) {
        return n.get(REPLYTO_KEY) + ":" + n.get(DESTINATION_KEY).toString() + ":" + n.get(TYPE_KEY).toString() + "[" + new Date() + "] " + ser++;
    }
}