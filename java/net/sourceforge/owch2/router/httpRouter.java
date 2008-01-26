package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;
import static net.sourceforge.owch2.kernel.Message.*;

import java.io.*;
import java.net.*;
import java.util.*;

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

    public void remove(String key) {
        elements.remove(key);
    }


    public String getDestination(Map<String, ?> item) {
        return String.valueOf(item.get(DESTINATION_KEY));
    }


    public Set getPool() {
        return elements.keySet();
    }


    public boolean hasPath(String key) {
        return elements.containsKey(key);
    }


    public boolean pathExists(Map<String, ?> item) {
        Location<String> met = new Location<String>();

        met.put(REPLYTO_KEY, item.get(REPLYTO_KEY).toString());
        met.put(URI_KEY, item.get(URI_KEY).toString());
        elements.put(item.get(REPLYTO_KEY).toString(), met);

        return true;
    }


    public void send(Map<String, ?> item) {
        MetaProperties n = new Message(item);
        if (n.getJMSReplyTo() == null) {
            return;
        }
//        String serr = n.get(Message.REPLYTO_KEY) + ":" + n.get("JMSDestination").toString() + ":" + n.get("JMSType").toString() +                "[" + d.toString() + "] " + ser++;

        Location location = ProtocolType.Http.getLocation();
        n.put(URI_KEY, location.getURI());
        MetaProperties prox = (MetaProperties) elements.get(n.get(DESTINATION_KEY));
        if (prox == null) {
            prox = (MetaProperties) Env.getInstance().getParentNode();
        }
        URI uri = null;
        try {
            uri = URI.create(String.valueOf(prox.get(URI_KEY)));
        } catch (Exception e) { //URL parse issues.
        }

        try {
            if (uri == null) {
                if (Env.getInstance().isParentHost()) {
                    return;
                } else {
                    uri = URI.create(Env.getInstance().getParentNode().getURI());
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


