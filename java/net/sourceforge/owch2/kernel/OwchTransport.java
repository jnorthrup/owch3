package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * One Way Communication Handler.  simple rfc822 properties packets.  1 message, 1 ACK, via UDP to neighbor node'subject URL.
 * <predicate/>
 * protocol   uses 'one way' messages.
 * <predicate/>
 * outboundowch messages contain          <ul>
 * <li>From:
 * <li>To:
 * <li> MessageId:</ul>
 * <predicate/>
 * owch-agents use reflection to fire methods named by
 * <ul><li>Type:</ul>
 * <predicate/>
 * when a peer receives a owch Notification, the response can be any owch message that delivers one or more properties
 * <ul><li>ACK: ${MessageId} ...</ul>
 * <predicate/>
 * <predicate> genuine windowed and streaming ACKS are not prevented by this model, however it'subject not the objective and one ACK
 * property in one UDP packet is perfectly fine.
 * <predicate>since owch is a hand-off and dispatch protocol designed more for introductions and routing updates, the
 * efficiency is in the terse nature of the messages and not in complex network code.
 * <predicate/>
 * <predicate>However, today'subject network traffic is not dominated by Tenex Telnet sessions. Even if we assume Request/Response
 * pairs averaging 20 and 40 bytes, this reduces the advantage to roughly 10%. If Request/Response pairs are 100 bytes,
 * the advantage is reduced to roughly 1%. So the advantage of piggybacking acks quickly becomes negligible. Given that
 * Internet traffic data shows significant modes at 40 bytes (syns and acks with no data), 576 bytes (default maximum
 * unfragmented size) and 1500 (Ethernet frames) would seem to imply that piggybacking is not as advantageous today as
 * it was in 1974.
 * <a href=http://safari.oreilly.com/9780132252423>        Patterns in Network Architecture: A Return to Fundamentals
 * ch. 7</a>
 */

class OwchTransport extends AbstractTransport {
    static final private RFC822Format rfc822Format = new RFC822Format();
    private static final int BLOCK_SIZE = Reactor.getInstance().getInstance().getCacheBuffer().capacity();
    private Queue<DatagramChannel> channelQ = new ConcurrentLinkedQueue<DatagramChannel>();
    private Queue<Quad<Transaction, CharSequence, SocketAddress, ByteBuffer>> outQ = new LinkedBlockingDeque<Quad<Transaction, CharSequence, SocketAddress, ByteBuffer>>();
    Exchanger<Quad<Transaction, CharSequence, SocketAddress, ByteBuffer>> sendX;


    public OwchTransport() {
        //unified for all sockets
        Exchanger<HasProperties> inX = new Exchanger<HasProperties>();
        Exchanger<HasProperties> outX = new Exchanger<HasProperties>();
        readX = new Exchanger<ByteBuffer>();
        writeX = new Exchanger<ByteBuffer>();
        sendX = new Exchanger<Quad<Transaction, CharSequence, SocketAddress, ByteBuffer>>();

    }


    void init() throws IOException {

        try {
            createChannel(new InetSocketAddress(2112));
        } catch (IOException e) {

        }
        for (int i = 0; i < getSockets(); i++) {
            createChannel(null);
        }

        //TODO: write the ACK outQ removal code and delay per owch message.
        Reactor.getInstance().submit(new Callable<Object>() {
            public Object call() throws Exception {
                while (true) {
                    final Quad<Transaction, CharSequence, SocketAddress, ByteBuffer> byteBufferQuad = outQ.remove();
                    outQ.add(byteBufferQuad);
                    sendX.exchange(byteBufferQuad);
                }
            }
        })
    }

    private void createChannel(InetSocketAddress addr) throws IOException {

        if (addr == null) {
            SocketAddress socketAddress = new InetSocketAddress(0); //wildcard
        }
        DatagramChannel datagramChannel = DatagramChannel.open();

        DatagramSocket socket = datagramChannel.socket();

        socket.setReceiveBufferSize(BLOCK_SIZE);
        socket.setSendBufferSize(BLOCK_SIZE);
        socket.setReuseAddress(true);
        socket.bind(addr);
        socket.setTrafficClass(
                /*
                 * IPTOS_LOWCOST (0x02)
                 * IPTOS_RELIABILITY (0x04)
                 * IPTOS_THROUGHPUT (0x08)
                 * IPTOS_LOWDELAY (0x10)
                 */
                0x14
        );
        channel = socket.getChannel();
        channel.configureBlocking(false);
        channelQ.add(channel);
    }


    public Quad<Transaction, CharSequence, SocketAddress, ByteBuffer> send(Transaction transaction) throws InterruptedException, URISyntaxException, SocketException {
        final URI uri = getPathMap().containsKey(transaction.getDestination()) ?
                getPathMap().get(transaction.getDestination()) :
                getPathMap().get("default");

        String host = getPathMap().containsKey(transaction.getDestination()) ?
                getPathMap().get(transaction.getDestination()).getHost() :
                Default.getURI().getHost();
        Quad<Transaction, CharSequence, SocketAddress, ByteBuffer>
                quad =
                new Quad<Transaction, CharSequence, SocketAddress, ByteBuffer>(
                        transaction,
                        transaction.getId(),
                        new InetSocketAddress(uri.getHost(), uri.getPort()),
                        rfc822Format.send(transaction).exchange(Reactor.getInstance().getCacheBuffer()));
        outQ.add(quad);
        return quad;
    }

    ;

//    public Quad<Transaction, CharSequence, SocketAddress, ByteBuffer> getNextOutboundTransaction() {
//        DatagramChannel c = (DatagramChannel) outQ.remove();
//        outQ.add(c);
//        return c;
//    }
//

    public int getPort() {
        return getNext().socket().getPort();
    }


    public DatagramChannel getNext() {
        DatagramChannel c = (DatagramChannel) channelQ.remove();
        channelQ.add(c);
        return c;
    }

    public int getSockets() {
        return sockets;
    }

    public void getNextOutboundTransaction() {
        //To change body of created methods use File | Settings | File Templates.
    }


    private class Triple<S, P, O> {
        private S subject;
        private P predicate;
        private O object;

        Triple(S subject, P predicate, O object) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
        }

        public S getSubject() {
            return subject;
        }

        public P getPredicate() {
            return predicate;
        }

        public O getObject() {
            return object;
        }
    }

    class Quad<C, S, P, O> {
        private C context;
        private S subject;
        private P predicate;
        private O object;

        Quad(C context, S subject, P predicate, O object) {
            this.context = context;
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
        }

        public S getSubject() {
            return subject;
        }

        public P getPredicate() {
            return predicate;
        }

        public O getObject() {
            return object;
        }

        public C getContext() {
            return context;
        }
    }
}
