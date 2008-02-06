package net.sourceforge.owch2.kernel;


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
public class AbstractTransport implements Transport {
    Transport local = new AbstractTransport() {
        public boolean hasPath(CharSequence name) {
            return localAgents.containsKey(name);
        }

        public Future<Exchanger<ByteBuffer>> send(final HasDestination notification) {
            Callable callable = new Callable() {
                public Object call() throws Exception {
                    throw new UnsupportedOperationException();
                }
            };
            Reactor.getInstance().submit(callable);
            return null;
        }
    };


    Transport http = new AbstractTransport() {
    };
    static Transport /**
     * Default's job is to deliver all message up to 'default' agent
     */
            Default = new AbstractTransport() {
    };
    Transport slab = new AbstractTransport() {
    };
    protected static DatagramChannel channel;
    private static Map<CharSequence, Agent> localAgents = new ConcurrentHashMap<CharSequence, Agent>();
    protected Map<CharSequence, URI> pathMap = new ConcurrentSkipListMap<CharSequence, URI>();
    private URI URI;
    private InetAddress hostAddress;
    private NetworkInterface hostInterface;
    private Short port;
    protected Integer sockets;
    private Integer threads;

    protected Exchanger<ByteBuffer> readX;

    protected Exchanger<ByteBuffer> writeX;
    private String name;
    private InetAddress localAddress;

    public Map<CharSequence, Agent> getLocalAgents() {
        return AbstractTransport.localAgents;
    }

    public URI getURI() throws URISyntaxException {
        String hostName = null;
        try {
            hostName = getLocalHostName();
        } catch (SocketException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return new URI(getName() + "://", null, hostName, port, null, null, null);
    }

    private String getLocalHostName() throws SocketException {
        InetAddress inetAddress = getLocalAddress();
        return inetAddress.getCanonicalHostName();
    }

    private InetAddress getLocalAddress() throws SocketException {
        if (localAddress != null) return localAddress;
        InetAddress inetAddress = null;
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        Collection<InetAddress> site = new ArrayList<InetAddress>();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isLoopback()) {
                Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses();

                while (inetAddressEnumeration.hasMoreElements()) {
                    inetAddress = inetAddressEnumeration.nextElement();
                    boolean sitelocal = inetAddress.isSiteLocalAddress();
                    if (sitelocal)
                        site.add(inetAddress);
                    else if (!(inetAddress.isAnyLocalAddress() |
                            inetAddress.isLoopbackAddress() |
                            inetAddress.isAnyLocalAddress() |
                            inetAddress.isMulticastAddress()))
                        return inetAddress;
                }
            }
        }
        return site.iterator().next();
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

    public int getPort() {
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

    public String getName() {
        return name;
    }

    public Map<CharSequence, URI> getPathMap() {
        return pathMap;
    }
}
