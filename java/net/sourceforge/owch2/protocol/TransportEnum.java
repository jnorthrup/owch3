package net.sourceforge.owch2.protocol;


import net.sourceforge.owch2.kernel.*;

import javax.lang.model.element.*;
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
 * @copyright All Rights Reserved Glamdring Inc.
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
//                    Future<Iterable<Map.Entry<CharSequence,Object>>> future = Reactor.submit(new Callable<Iterable<Map.Entry<CharSequence, Object>>>() {
//                        public Iterable<Map.Entry<CharSequence, Object>>
//                        call() throws Exception {
//                            final Future<Iterable<Map.Entry<CharSequence, Object>>> future1 = format.recv(readX);
//                            return future1.get();
//                        }
//                    });
                    final Future<Iterable<Map.Entry<CharSequence, Object>>> iterableFuture = format.recv(readX);

                    ByteChannel iChannel = (ByteChannel) key.channel();
                    ByteBuffer buffer = Reactor.getCacheBuffer();
                    int i = iChannel.read(buffer);
                    final ByteBuffer buffer1 = readX.exchange(buffer);

                    Env.getInstance().recv(new DefaultMapTransaction(iterableFuture.get()));
                    return true;
                }

                ;


                public boolean channelWrite(SelectionKey key) throws InterruptedException, IOException {
                    ByteBuffer buffer = writeSwap.exchange(Reactor.getCacheBuffer());
                    ByteChannel channel1 = (ByteChannel) key.channel();
                    int i = channel1.write(buffer);
                    return false;
                }
            };

        }
    },
    http,
    Null;
    private static DatagramChannel channel;
    private static Map<CharSequence, Agent> localAgents = new ConcurrentHashMap<CharSequence, Agent>();
    private ConcurrentSkipListMap<Name, URI> pathMap = new ConcurrentSkipListMap<Name, URI>();
    private URI URI;
    private InetAddress hostAddress;
    private NetworkInterface hostInterface;
    private Short port;
    private Integer sockets;
    private Integer threads;
    /**
     robust {public void init() {
     //To change body of implemented methods use File | Settings | File Templates.
     }},
     http {public void init() {
     //To change body of implemented methods use File | Settings | File Templates.
     }},
     Domain {public void init() {
     //To change body of implemented methods use File | Settings | File Templates.
     }},
     multicast {public void init() {
     //To change body of implemented methods use File | Settings | File Templates.
     }},
     Default {public void init() {
     //To change body of implemented methods use File | Settings | File Templates.
     }},
     Null {public void init() {
     //To change body of implemented methods use File | Settings | File Templates.
     }},
     RFC822 {public void init() {
     //To change body of implemented methods use File | Settings | File Templates.
     }},*/
    ;


    public Map<CharSequence, Agent> getLocalAgents() {
        return localAgents;
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
