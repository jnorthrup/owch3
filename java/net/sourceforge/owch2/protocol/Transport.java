package net.sourceforge.owch2.protocol;

import net.sourceforge.owch2.kernel.Agent;
import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.kernel.EventDescriptor;
import net.sourceforge.owch2.protocol.router.NullRouter;
import net.sourceforge.owch2.protocol.router.Router;
import net.sourceforge.owch2.protocol.router.ipcRouter;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectableChannel;
import java.util.EnumMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

/**
 * these are transports
 *
 * @author James Northrup
 * @version $Id$
 * @copyright All Rights Reserved Glamdring Inc.
 */
public enum Transport implements Router {
    ipc {
        public void init() {
            super.init();
            this.getOutboundEventAgenda().put(OutboundLifecycle.route, new EventTask() {
                public Callable getCallable(final EventDescriptor event) {
                    return new Callable() {
                        public Object call() throws Exception {
                            return ((ipcRouter) router).getLocalAgents().containsKey(event.getDestination());
                        }
                    };
                }
            });
            this.getInboundEventAgenda().put(InboundLifeCycle.route, new EventTask() {
                public Callable getCallable(final EventDescriptor event) {
                    return new Callable() {
                        public Object call() throws Exception {
                            ipcRouter r = (ipcRouter) router;
                            final Agent agent = r.getLocalAgents().get(event.getDestination());
                            agent.recv(event);
                            return true;
                        }
                    };
                }
            });
        }
    },
    owch,
    http,
    Domain,
    multicast,
    Default,
    Null,
    RFC822 {
        public void init() {
            router = new NullRouter();
        }},;


    Router router;
    InetAddress hostAddress;
    NetworkInterface hostInterface;
    int port = -1;
    Integer sockets;
    Integer threads;

    private EnumMap<OutboundLifecycle, EventTask> outboundEventAgenda;
    private EnumMap<InboundLifeCycle, EventTask> inboundEventAgenda;


    Transport() {
        inboundEventAgenda = new EnumMap<InboundLifeCycle, EventTask>(InboundLifeCycle.class);
        outboundEventAgenda = new EnumMap<OutboundLifecycle, EventTask>(OutboundLifecycle.class);
        this.init();
    }


    public void init() {
        String routerClassName = null;
        try {
            routerClassName = getClass().getPackage().getName() + ".router." + name() + "Router";
            Class routerClass = Class.forName(routerClassName);
//            this.router = (Router) routerClass.newInstance();
            Constructor constructor = routerClass.getConstructor(Transport.class);
            router = (Router) constructor.newInstance(this);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Error("Router mismatch finding " + routerClassName);
        }

    }

    public Router getRouter() {
        return router;
    }

    public URI getURI() {
        try {
            /*
            I(String scheme,
           String userInfo,
           String host,
           int port,
           String path,
           String query,
           String fragment)
             */
            String canonicalName = getClass().getCanonicalName() + "." + name();
            String hostName = Env.getInstance().getHostAddress().getCanonicalHostName();
            String name1 = canonicalName.replaceAll(".", "/") + ".class";
            String path =
                    "/" + java.net.URLEncoder.encode(ClassLoader.getSystemClassLoader().getResource(getClass().getCanonicalName().replaceAll("\\.", "/") + ".class").toString());
            String username = hostInterface == null ? null : hostInterface.getDisplayName();
            String scheme = name();

            final URI uri = new URI(scheme, canonicalName, hostName, port, path, null, username);
            System.out.println(uri.toASCIIString());
            return uri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
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

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public void setSockets(Integer sockets) {
        this.sockets = sockets;
    }

    public int getPort() {
        return port;
    }

    public ConcurrentMap<String, URI> getPathMap() {
        return router.getPathMap();
    }

    public URI getPath(EventDescriptor destination) {
        return router.getPath(destination);
    }

    public boolean hasPath(EventDescriptor location) {
        return router.hasPath(location);
    }

    public Future<Receipt> route(EventDescriptor... async) throws Exception {
        return router.route(async);
    }


    public URI remove(String jmsReplyTo) {
        return router.remove(jmsReplyTo);
    }

    /**
     * inbound messages enter here from the net.  transport-specific metadata is gathered about this socket
     *
     * @param byteChannel from "accept"
     * @return boolean telling the reactor to proceed adding or to cancel the Selectable
     */
    public boolean channelAccept(ByteChannel byteChannel) {
        return true;
    }

    /**
     * @param channel
     * @return reinit in the reactor or remove?
     */
    public boolean channelConnect(SelectableChannel channel) {
        return true;
    }


    public boolean channelRead(ByteChannel channel) {
        return true;
    }

    public boolean channelWrite(ByteChannel byteChannel) {
        return true;
    }

    public boolean hasPath(String destination) {
        return getPathMap().containsKey(destination);
    }

    public Callable getInboundEventAgendaTask(InboundLifeCycle inboundLifeCycle, EventDescriptor event) {
        EventTask task = getInboundEventAgenda().get(inboundLifeCycle);
        return task.getCallable(event);
    }

    public void addReceipt(Receipt receipt) {

    }

    public EnumMap<OutboundLifecycle, EventTask> getOutboundEventAgenda() {
        return outboundEventAgenda;
    }

    public EnumMap<InboundLifeCycle, EventTask> getInboundEventAgenda() {
        return inboundEventAgenda;
    }

    public Transport addResult(EventDescriptor event, Enum key, Object result) {
        return this;
    }
}
