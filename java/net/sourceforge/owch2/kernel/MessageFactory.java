package net.sourceforge.owch2.kernel;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.util.*;

/**
 * This converts (injects) network data into a factory that produces Messages.
 *
 * @author James Northrup
 * @version $Id: MessageFactory.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
final public class MessageFactory implements Runnable, DatagramPacketFilter, StreamFilter {
    private final Set<? extends SoftReference<? extends String>> recv = new HashSet<SoftReference<? extends String>>();
    private ReferenceQueue q = new ReferenceQueue();
    private DatagramSocket ds;
    private static MessageFactory instance;

    public final void recv(InputStream reader) {
        try {
            MetaProperties n = new Message();
            n.load(reader);
            boolean more = ackPacket(n);
            if (more) {
                routePacket((Message) n);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final boolean ackPacket(MetaProperties n) throws IOException, SocketException {
        String s = (String) n.get("ACK"); //priority notification
        //check for ACK
        if (s != null) {

            owchDispatch.getInstance().remove(s);
            return false;
        }
        if (n.getJMSReplyTo() == null) {
            throw new IOException("MessageFactory has been sent a deformed Message.");
        }
        s = (String) n.get("JMSMessageID");
        if (s != null) {
//            URLString uri = new URLString(n.getURI());
            MetaProperties n2 = new Message();
            URI uri = URI.create(n.getURI());
            n2.put("ACK", s);
            String h = uri.getHost();
            InetAddress dest = InetAddress.getByName(h);
            OutputStream os = new ByteArrayOutputStream();
            n2.save(os);
            byte buf[] = os.toString().getBytes();
            //create the datagram
            DatagramPacket p = new DatagramPacket(buf, buf.length, dest, uri.getPort());
            //grab an owch listener and send with it
            //DatagramSocket ds = (DatagramSocket)Env.getInstance().getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
            ds.send(p);
            return true;
        }
        return false;
    }

    /**
     * check for dupes
     *
     * @param message something that should have a Message ID
     */
    public final void routePacket(MetaProperties message) {
        if (!recognize(message, (String) message.get(Message.MESSAGE_ID_KEY))) {
            Env.getInstance().send(message);
        }
    }

    public boolean recognize(MetaProperties n, Object s) {
        boolean res = false;
        SoftReference<? extends String> ref;
        synchronized (recv) {
            Iterator<? extends SoftReference<? extends String>> i = recv.iterator();
            while (i.hasNext()) {
                ref = i.next();
                String prev = ref.get();
                if (prev == null) {
                    continue;
                }
                if (prev.equals(s)) {
                    res = true;
                    break;
                }
            }
        }
        return res;
    }

    public MessageFactory() {
        try {
            ds = new DatagramSocket(0);
            Thread t = new Thread();
            t.setDaemon(true);
            t.start();
        } catch (SocketException e) {
            e.printStackTrace();  //!TODO: review for fit
        }
    }

    public void recv(DatagramPacket p) {
        InputStream istream = new ByteArrayInputStream(p.getData());
        try {
            recv(istream);
            istream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            Reference ref;
            while (!Env.getInstance().shutdown) {
                ref = q.remove(3000L); //3 seconds
                if (ref != null) {
                    synchronized (recv) {
                        recv.remove(ref);
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatagramPacketFilter getInstance() {
        if (null == instance)
            instance = new MessageFactory();

        return instance;
    }

}


