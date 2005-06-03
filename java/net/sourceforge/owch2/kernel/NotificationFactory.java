package net.sourceforge.owch2.kernel;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * @author James Northrup
 * @version $Id: NotificationFactory.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
final public class NotificationFactory implements Runnable, DatagramPacketFilter, StreamFilter {
    private Set recv = new HashSet();
    private ReferenceQueue q = new ReferenceQueue();
    private DatagramSocket ds;
    private static NotificationFactory instance;

    public final void recv(InputStream reader) {
        try {
            MetaProperties n = new Notification();
            n.load(reader);
            boolean more = ackPacket(n);
            if (more) {
                routePacket(n);
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
            if (Env.getInstance().logDebug)
                Logger.global.info("NotificationFactory.handleStream() ACK Notification: " + s);

            owchDispatch.getInstance().remove(s);
            return false;
        }
        if (n.getJMSReplyTo() == null) {
            throw new IOException("NotificationFactory has been sent a deformed Notification.");
        }
        s = (String) n.get("JMSMessageID");
        if (s != null) {
//            URLString uri = new URLString(n.getURI());
            MetaProperties n2 = new Notification();
            URI uri = n.getURI();
            n2.put("ACK", s);
            String h = uri.getHost();
            InetAddress dest = InetAddress.getByName(h);
            OutputStream os = new ByteArrayOutputStream();
            n2.save(os);
            byte buf[ ] = os.toString().getBytes();
            //create the datagram
            DatagramPacket p = new DatagramPacket(buf, buf.length, dest, uri.getPort());
            //grab an owch listener and send with it
            //DatagramSocket ds = (DatagramSocket)Env.getInstance().getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
            ds.send(p);
            return true;
        }
        return false;
    }

    public final void routePacket(MetaProperties n) {
        String s = (String) n.get("JMSMessageID");
        if (!recognize(n, s)) {
            Env.getInstance().send(n);
        }
    }

    public boolean recognize(MetaProperties n, String s) {
        boolean res = false;
        SoftReference ref;
        synchronized (recv) {
            Iterator i = recv.iterator();
            while (i.hasNext()) {
                ref = (SoftReference) i.next();
                String prev = (String) ref.get();
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

    public NotificationFactory() {
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
        ByteArrayInputStream istream = new ByteArrayInputStream(p.getData());
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
                        if (Env.getInstance().logDebug)
                            Logger.global.info(getClass().getName() + "::collecting softref ---- ");
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static NotificationFactory getInstance() {
        if (null == instance)
            instance = new NotificationFactory();

        return instance;
    }

}


