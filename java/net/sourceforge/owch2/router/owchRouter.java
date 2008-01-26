package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;
import static net.sourceforge.owch2.kernel.Location.URI_KEY;
import static net.sourceforge.owch2.kernel.Message.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public class owchRouter implements Router {
    static long ser = 0;
    private Map<String, MetaAgent> proxies = new TreeMap<String, MetaAgent>();

    public void remove(String key) {
        proxies.remove(key);
    }


    public String getDestination(Map<String, ?> item) {
        return String.valueOf(item.get(DESTINATION_KEY));
    }

    public Set getPool() {
        return proxies.keySet();
    }


    public boolean hasPath(String key) {
        return proxies.containsKey(key);
    }


    public boolean pathExists(Map<String, ?> item) {
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
            e.printStackTrace();
        }
        proxies.put(item.get(REPLYTO_KEY).toString(), location);
    }

    public void send(Map<String, ?> item) {
        Message message = new Message(item);
        if (message.getJMSReplyTo() == null) return;

        final String serial = createSerialNumber(message);
        MetaProperties outProx = PrepareDelivery(message, serial);
        URI destinationURI = null;
        if (outProx.containsKey(URI_KEY)) {
            destinationURI = URI.create(String.valueOf(outProx.get(URI_KEY)));
        } else {
            if (!Env.getInstance().isParentHost()) {
                destinationURI = URI.create(Env.getInstance().getParentNode().getURI());
            } else {
                Logger.getAnonymousLogger().info("******Domain:  DROPPING PACKET FOR " + outProx.get(REPLYTO_KEY));
                return;
            }
        }


        try {
            byte[] buf;
            buf = createByteBuffer(message);
            DatagramPacket p =
                    new DatagramPacket(buf, buf.length, InetAddress.getByName(
                            destinationURI.getHost()),
                            destinationURI.getPort());
            owchDispatch.getInstance().handleDatagram(serial, p, message.get(PRIORITY_KEY) != null);
        }
        catch (UnknownHostException e) {
        }
        catch (IOException e) {
        }
    }

    private MetaProperties PrepareDelivery(Map n, String serial) {
        n.put(MESSAGE_ID_KEY, serial);

        MetaProperties l = ProtocolType.owch.getLocation();
        n.put(URI_KEY, l.getURI());
        MetaProperties outProx = getProxy(n);
        return outProx;
    }

    public static byte[] createByteBuffer(MetaProperties n) throws IOException {
        OutputStream os = new ByteArrayOutputStream();
        n.save(os);
        byte[] buf = os.toString().getBytes();
        return buf;
    }

    private MetaProperties getProxy(Map n) {
        MetaProperties prox = (MetaProperties) proxies.get(n.get(DESTINATION_KEY));
        if (prox != null) {
            return prox;
        }
        prox = (MetaProperties) Env.getInstance().getParentNode();
        return prox;
    }

    private static String createSerialNumber(Map n) {
        return n.get(REPLYTO_KEY) + ":" + n.get(DESTINATION_KEY).toString() + ":" + n.get(TYPE_KEY).toString() + "[" + new Date() + "] " + ser;
    }
}