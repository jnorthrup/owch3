package net.sourceforge.owch2.kernel;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * owchListener.java
 *
 * @author James Northrup
 * @version $Id: owchListener.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class owchListener extends UDPServerWrapper implements Runnable, ListenerReference,
        DatagramPacketSource {
    private int threads;

    public owchListener(InetAddress hostAddr, int port, int threads) throws SocketException {
        super(hostAddr, port);
        this.threads = threads;
//        Auto.attach(this, Env.getInstance() .getNotificationFactory());
        attach(
                Env.getInstance() .getNotificationFactory());
    }

    ;

    public owchListener(InetAddress hostAddr, int port) throws SocketException {
        super(hostAddr, port);
    }

    ;

    public final void run() {
//        if (Env.logDebug) Env.log(20, "debug: " + Thread.currentThread().getName() + " init");
        byte bar [ ] = new byte[32768];
        while (true) {
            try {
                DatagramPacket p = new DatagramPacket(bar, bar.length);
                receive(p);
                data = p;
                xmit();
//                if (Env.logDebug) Env.log(12, "debug: spin, " + Thread.currentThread().getName());
            }
            catch (IOException e) {
//                if (Env.logDebug) Env.log(5, "debug: OWCH RUN BREAK");
                break;
            }
        }
//        if (Env.logDebug) Env.log(5, "debug: OWCH THREAD STOP");
    }

    ;

    public String getProtocol() {
        return "owch";
    }

    ;

    public long getExpiration() {
        return (long) 0;
    }

    public int getThreads() {
        return this.threads;
    }

    public ServerWrapper getServer() {
        return this;
    }

    ;

    public void expire() {
        getServer().close();
    }

    ;

    private java.util.List _DatagramPacket_clients = new ArrayList();

    public void attach(DatagramPacketFilter filter) {
        _DatagramPacket_clients.add(filter);
    }

    ;

    public void detach(DatagramPacketFilter filter) {
        _DatagramPacket_clients.remove(filter);
    }

    ;

    private DatagramPacket data;

    private static final Class[] parm_cls_DatagramPacket = new Class[]{
        DatagramPacket.class
    };

    public void xmit() {
        Iterator iter = _DatagramPacket_clients.iterator();
        while (iter.hasNext()) {
            DatagramPacketFilter filter = (DatagramPacketFilter) iter.next();
            filter.recv(data);
        }
    }

    ;
}


