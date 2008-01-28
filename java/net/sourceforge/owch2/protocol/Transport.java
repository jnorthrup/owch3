package net.sourceforge.owch2.protocol;

import net.sourceforge.owch2.kernel.*;
import net.sourceforge.owch2.protocol.router.*;

import java.net.*;
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
public enum Transport implements Router {
    ipc,
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

    private Map<Enum, EventTask> eventAgenda = new IdentityHashMap<Enum, EventTask>(InboundLifeCycle.values().length + OutboundLifecycle.values().length);

    Transport() {
        this.init();
    }

    public void init() {
        String routerClassName = null;
        try {
            routerClassName = getClass().getPackage().getName() + ".router." + name() + "Router";
            Class routerClass = Class.forName(routerClassName);
            this.router = (Router) routerClass.newInstance();
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
            e.printStackTrace();  //ToDo: change body of catch statement use File | Settings | File Templates.
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

    public Future<Receipt> send(EventDescriptor... async) throws Exception {
        return router.send(async);
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

    public Callable getEventAgendaTask(InboundLifeCycle inboundLifeCycle, EventDescriptor event) {
        EventTask task = eventAgenda.get(inboundLifeCycle);
        return task.getCallable(event);
    }

    public void addReceipt(Receipt receipt) {

    }
}
