package net.sourceforge.owch2.kernel;


import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;


/**
 * these are transports
 *
 * @author James Northrup
 * @version $Id$
 */
@SuppressWarnings({"ALL"})
public enum TransportEnum implements Transport {
    local {
        public boolean hasPath(CharSequence name) {
            return localAgents.containsKey(name);
        }

        public Future<Exchanger<ByteBuffer>> send(final HasDestination notification) {
            Callable callable = new Callable() {
                public Object call() throws Exception {
                    throw new UnsupportedOperationException();
                }
            };
            Reactor.submit(callable);
            return null;
        }},
    owch {
        void init() {

            new ChannelController() {
                RFC822Format format = new RFC822Format();

                private Exchanger<ByteBuffer> readX = new Exchanger<ByteBuffer>();
                private Exchanger<ByteBuffer> writeX = new Exchanger<ByteBuffer>();

                public URI getUri() throws SocketException, URISyntaxException {
                    return getURI();
                }

                public void init(Exchanger<ByteBuffer> swap) {
                    this.readX = swap;
                    try {
                        DatagramSocket socket = new DatagramSocket();
                        socket.setReceiveBufferSize(Reactor.BUFFSIZE);
                        socket.setSendBufferSize(Reactor.BUFFSIZE);
                        socket.setReuseAddress(true);
                        SocketAddress socketAddress = new InetSocketAddress(getHostAddress(), getPort());
                        socket.bind(socketAddress);
                        socket.setTrafficClass(
                                /**
                                 * IPTOS_LOWCOST (0x02)
                                 IPTOS_RELIABILITY (0x04)
                                 IPTOS_THROUGHPUT (0x08)
                                 IPTOS_LOWDELAY (0x10)
                                 */
                                0x14
                        );
                        channel = socket.getChannel();
                        channel.configureBlocking(false);
                        SelectionKey selectionKey = channel.register(Reactor.getSelector(), channel.validOps(), this);

                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }

                public boolean channelAccept(SelectionKey key) {
                    throw new UnsupportedOperationException();
                }

                public boolean channelConnect(SelectionKey key) {
                    throw new UnsupportedOperationException();
                }


                public boolean channelRead(SelectionKey key) throws ExecutionException, InterruptedException, IOException {
                    final Future<Iterable<Map.Entry<CharSequence, Object>>> iterableFuture = format.recv(readX);

                    ByteChannel iChannel = (ByteChannel) key.channel();
                    ByteBuffer buffer = Reactor.getCacheBuffer();
                    int i = iChannel.read(buffer);
                    final ByteBuffer buffer1 = readX.exchange(buffer);

//                    Env.getInstance().recv(new DefaultMapTransaction(iterableFuture.get()));
                    return true;
                }

                public boolean channelWrite(SelectionKey key) throws InterruptedException, IOException {
                    ByteBuffer buffer = writeX.exchange(Reactor.getCacheBuffer());
                    ByteChannel channel1 = (ByteChannel) key.channel();
                    int i = channel1.write(buffer);
                    return false;
                }
            };

        }
    },
    http,
    /**
     * Default's job is to deliver all message up to 'default' agent
     */
    Default,
    slab {};
    private static DatagramChannel channel;
    private static Map<CharSequence, Agent> localAgents = new ConcurrentHashMap<CharSequence, Agent>();
    private ConcurrentSkipListMap<CharSequence, URI> pathMap = new ConcurrentSkipListMap<CharSequence, URI>();
    private URI URI;
    private InetAddress hostAddress;
    private NetworkInterface hostInterface;
    private Short port;
    private Integer sockets;
    private Integer threads;


    public Map<CharSequence, Agent> getLocalAgents() {
        return TransportEnum.localAgents;
    }

    public URI getURI() throws URISyntaxException {
        String hostName = null;
        try {
            hostName = getLocalHostName();
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return new URI(name() + "://", null, hostName, port, null, null, null);
    }

    private String getLocalHostName() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        String hostName = "localhost";
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback()) continue;
            Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();
            while (inetAddressEnumeration.hasMoreElements()) {
                InetAddress inetAddress = inetAddressEnumeration.nextElement();
                hostName = inetAddress.getCanonicalHostName();
            }
        }
        return hostName;
    }

    public void setHostAddress(InetAddress hostAddress) {
        this.hostAddress = hostAddress;
    }

    public void setHostInterface(NetworkInterface hostInterface) {
        this.hostInterface = hostInterface;
    }

    public void setPort(Short port) {
        this.port = port;
    }

    public void setSockets(Integer sockets) {
        this.sockets = sockets;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public Format getFormat() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    public void recv(HasDestination notification) {
        if (getLocalAgents().containsKey(notification.getDestination())) {
            Agent agent = getLocalAgents().get(notification.getDestination());
            agent.recv(notification);
        }
    }

    public Short getPort() {
        return port;
    }

    public boolean hasPath(CharSequence name) {
        return pathMap.containsKey(name);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Future<Exchanger<ByteBuffer>> send(HasDestination notification) {

        return null;
    }

    public NetworkInterface getHostInterface() {
        return hostInterface;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }
}
