package net.sourceforge.owch2.router;

import static net.sourceforge.owch2.kernel.Env.getInstance;
import net.sourceforge.owch2.kernel.Location;
import net.sourceforge.owch2.kernel.MetaProperties;
import net.sourceforge.owch2.kernel.Notification;
import net.sourceforge.owch2.kernel.URLString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author James Northrup
 * @version $Id: owchRouter.java,v 1.1 2005/06/01 06:43:12 grrrrr Exp $
 */
public class owchRouter implements Router {
    static long ser = 0;
    private Map elements = new TreeMap();

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

        Location location = new Location();
        decorateProxy(location, item);
        return true;
    }

    private void decorateProxy(Location location, Map item) {
        location.put("JMSReplyTo", item.get("JMSReplyTo").toString());
        try {
            location.put("URL", item.get("URL").toString());
        }
        catch (Exception e) {
        }
        elements.put(item.get("JMSReplyTo"), location);
    }

    public void send(Map item) {
        Notification n = new Notification(item);
        if (n.getJMSReplyTo() == null) {
            return;
        }
        final String serial = serr(n);
        MetaProperties outProx = PrepareDelivery(n, serial);
        String u;

        if (!outProx.containsKey("URL")) {
            if (getInstance().isParentHost()) {
                if (getInstance().logDebug)
                    getInstance().log(2, "******Domain:  DROPPING PACKET FOR " + outProx.get("JMSReplyTo"));
                return;
            } else {
                u = getInstance().getParentNode().getURL();
            }
        } else {
            u = outProx.get("URL").toString();
        }


        URLString url = new URLString(u);
        try {
            byte[] buf = createByteBuffer(n);
            DatagramPacket p = new DatagramPacket(buf, buf.length, dest(h(url)), url.getPort());
            getInstance().getowchDispatch().handleDatagram(serial, p, n.get("Priority") != null);
        }
        catch (IOException e) {
        }


    }

    private MetaProperties PrepareDelivery(Notification n, final String serial) {
        n.put("JMSMessageID", serial);
        n.put("URL", getInstance().getLocation("owch").getURL());
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
        MetaProperties prox = (MetaProperties) elements.get(n.get("JMSDestination"));
        if (prox == null) {
            prox = (MetaProperties) getInstance().getParentNode();
        }
        return prox;
    }

    private String serr(Notification n) {
        return n.get("JMSReplyTo") + ":" + n.get("JMSDestination").toString() + ":" + n.get("JMSType").toString() +
                "[" + d().toString() + "] " + ser++;
    }

    private Date d() {
        return new Date();
    }

    private String h(URLString url) {
        return url.getHost();
    }

    private InetAddress dest(String h) throws UnknownHostException {
        return InetAddress.getByName(h);
    }
}