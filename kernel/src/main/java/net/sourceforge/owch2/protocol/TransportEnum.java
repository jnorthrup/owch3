package net.sourceforge.owch2.protocol;


import net.sourceforge.owch2.kernel.*;


import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
 import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.logging.Logger;


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

        public Future<Exchanger<ByteBuffer>> send(final Notification notification) {
            Callable callable = new Callable() {
                public Object call() throws Exception {
                    localAgents.get(notification.getFrom()).recv(notification);
                    return null;
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
                private Exchanger<ByteBuffer> writeSwap = new Exchanger<ByteBuffer>();

                public URI getUri() {
                    return getURI();  //To change body of implemented methods use File | Settings | File Templates.
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
                    return false;
                }

                public boolean channelConnect(SelectionKey key) {
                    return false;
                }


                public boolean channelRead(SelectionKey key) throws ExecutionException, InterruptedException, IOException {
                    final Future<Iterable<Map.Entry<CharSequence, Object>>> iterableFuture = format.recv(readX);

                    ByteChannel iChannel = (ByteChannel) key.channel();
                    ByteBuffer buffer = Reactor.getCacheBuffer();
                    int i = iChannel.read(buffer);
                    final ByteBuffer buffer1 = readX.exchange(buffer);

                    Env.Companion.getInstance().recv(new DefaultMapTransaction(iterableFuture.get()));
                    return true;
                }

                public boolean channelWrite(SelectionKey key) throws InterruptedException, IOException {
                    ByteBuffer buffer = writeSwap.exchange(Reactor.getCacheBuffer());
                    ByteChannel channel1 = (ByteChannel) key.channel();
                    int i = channel1.write(buffer);
                    return false;
                }
            };

        }
    },
    /** http[s] post semantics */http ,
    /**
     * Default's job is to deliver all message up to 'default' agent
     */
    Default {
        public boolean hasPath(final CharSequence name) {
            if (!Env.getInstance().isParentHost()) {
                owch.pathMap.putIfAbsent(name, Env.getInstance().getDefaultURI());
                return true;
            }

            Logger.getAnonymousLogger().warning("Creating Queue Agent for " + name);
            Agent agent = new AbstractAgent() {
                {
                    Set<Notification> set = new LinkedHashSet<Notification>();
                    put("BackLog", set);

                }

                public void recv(Notification n) {
                    Set set = (Set) get("BackLog");
                    set.add(n);
                }

                public CharSequence getFrom() {
                    return name;
                }
            };
            return true;
        };
    }, Null;
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

    public URI getURI() {
        return URI;
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

    public void recv(Notification notification) {

    }

    public Short getPort() {
        return port;
    }

    public boolean hasPath(CharSequence name) {
        return pathMap.containsKey(name);  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Future<Exchanger<ByteBuffer>> send(Notification notification) {

        return null;
    }

    ;

    public NetworkInterface getHostInterface() {
        return hostInterface;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }
}
