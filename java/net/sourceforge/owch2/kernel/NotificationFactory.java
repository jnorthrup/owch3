package net.sourceforge.owch2.kernel;

import net.sourceforge.idyuts.IOLayer.*;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.util.*;

/**
 * @version $Id: NotificationFactory.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
final public class NotificationFactory implements Runnable, DatagramPacketFilter, StreamFilter {

    private Set recv = new HashSet();
    private ReferenceQueue q = new ReferenceQueue();
    private DatagramSocket ds;

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
            if (Env.logDebug) Env.log(13, "NotificationFactory.handleStream() ACK Notification: " + s);
            Env.getowchDispatch().remove(s);
            return false;
        }
        ;
        if (n.getJMSReplyTo() == null) {
            throw new IOException("NotificationFactory has been sent a deformed Notification.");
        }
        s = (String) n.get("JMSMessageID");
        if (s != null) {
            URLString url = new URLString(n.getURL());
            MetaProperties n2 = new Notification();
            n2.put("ACK", s);
            String h = url.getHost();
            InetAddress dest = InetAddress.getByName(h);
            OutputStream os = new ByteArrayOutputStream();
            n2.save(os);
            byte buf[ ] = os.toString().getBytes();
            //create the datagram
            DatagramPacket p = new DatagramPacket(buf, buf.length, dest, url.getPort());
            //grab an owch listener and send with it
            //DatagramSocket ds = (DatagramSocket)Env.getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
            ds.send(p);
            return true;
        }
        return false;
    }

    public final void routePacket(MetaProperties n) {
        String s = (String) n.get("JMSMessageID");
        if (!recognize(n, s)) {
            Env.send(n);
        }
        ;
    };

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
                ;
            }
            ;
        }
        ;
        return res;
    };


    public NotificationFactory() throws SocketException {
        ds = new DatagramSocket(0);
        Thread t = new Thread();
        t.setDaemon(true);
        t.start();
    };

    public void recv(DatagramPacket p) {
        ByteArrayInputStream istream = new ByteArrayInputStream(p.getData());
        try {
            recv(istream);
            istream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        ;
    };

    public void run() {
        try {
            Reference ref;
            while (!Env.shutdown) {
                ref = q.remove(3000L); //3 seconds
                if (ref != null) {
                    synchronized (recv) {
                        recv.remove(ref);
                        if (Env.logDebug) Env.log(40, getClass().getName() + "::collecting softref ---- ");
                    }
                }
                ;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ;
    };
}


