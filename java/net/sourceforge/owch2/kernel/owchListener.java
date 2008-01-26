package net.sourceforge.owch2.kernel;


import java.io.*;
import java.net.*;
import java.util.*;

/**
 * owchListener.java
 *
 * @author James Northrup
 * @version $Id: owchListener.java,v 1.4 2005/06/04 02:26:24 grrrrr Exp $
 */
public class owchListener extends UDPServerWrapper implements Runnable, ListenerReference,
        DatagramPacketSource {
    private int threads;

    public owchListener(InetAddress hostAddr, int port, int threads) throws SocketException {
        super(hostAddr, port);
        this.threads = threads;

        attach(
                MessageFactory.getInstance());
    }

    public owchListener(InetAddress hostAddr, int port) throws SocketException {
        super(hostAddr, port);
    }

    public final void run() {
        byte bar[] = new byte[32768];
        while (true) {
            try {
                DatagramPacket p = new DatagramPacket(bar, bar.length);
                receive(p);
                data = p;
                xmit();
            }
            catch (IOException e) {
                break;
            }
        }
    }

    public ProtocolType getProtocol() {
        return ProtocolType.owch;
    }


    public long getExpiration() {
        return (long) 0;
    }

    public int getThreads() {
        return this.threads;
    }

    public ServerWrapper getServer() {
        return this;
    }

    public void expire() {
        getServer().close();
    }

    private Collection _DatagramPacket_clients = new ArrayList();

    public void attach(DatagramPacketFilter filter) {
        _DatagramPacket_clients.add(filter);
    }

    public void detach(Object filter) {
        _DatagramPacket_clients.remove(filter);
    }

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
}


