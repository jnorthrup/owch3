package net.sourceforge.owch2.kernel;

import static net.sourceforge.owch2.kernel.ImmutableNotification.*;
import net.sourceforge.owch2.protocol.*;

import javax.script.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Mobile Agent Hosting Environment. This class acts as a hub for realtime messages
 * passing among several namespaces and transports.
 * <p/>
 * This also acts as the central network-stack kernel to unify the interfaces and routing of the various transports
 * that messages are destined for.
 * <p/>
 * Workflow design is facilitated by decoupling the URI and URL endpoints from the
 * agent identifiers (labeled <B>"JMSReplyTo"</B>).  Agents are semantically named with short human-readable ID's in order to
 * facilitate generic service names living among cloned, replicated, and mobile agents, who will
 * always communicate via the nearest agent hop named "default" initially to locate direct transport locations to
 * route to.
 * <p/>
 * "default" Agent routing is bootstrapped into an agent host and all traffic of unknown destination path will
 * forward to the agent named "default", typically a  domain object.
 * <p/>
 * Owch messages are intended to use multiple, transport specific fields, not solely any single syntax or
 * URI convention.
 * <p/>
 * the defacto delivery model is non-escaped, non multiline RFC822 with no serialization facilities in order to
 * keep the scope and the footprint simple. that said, some amount of REST and SMTP rfc822 usage may test this resolve.
 *
 * @author James Northrup
 * @version $Id$
 * @see AbstractAgent
 */
public class Env implements Invocable {
    volatile public boolean shutdown = false;
    private boolean parentFlag = false;
    private int owchPort = 0;
    private int httpPort = 0;
    private int hostThreads = 2;
    private int socketCount = 2;
    private String domainName = "default";

    private Map<String, Format> formatCache;


    /**
     * returns a Notification suitable for parent routing.
     */
    HasOrigin parentNode = null;

//    private PathResolver pathResolver = new LeafPathResolver();

    private InetAddress hostAddress;
    private NetworkInterface hostInterface;

    private static Env instance;
    private int pipePort;
    public static Map<String, Socket> httpdSockets = new ConcurrentHashMap<String, Socket>();
    private httpRegistry httpRegistry;
    private static Transport[] inboundTransports;
    private static Transport[] outboundTransports;
    private static Set<Agent> localAgents = new ConcurrentSkipListSet<Agent>();

    private Env() {
    }


    public int getOwchPort() {
        return owchPort;
    }

    public void setOwchPort(int port) {
        owchPort = port;
    }

    public int getHostThreads() {
        return hostThreads; //
    }

    public void setHostThreads(int t) {
        hostThreads = t;
    }

    public int getSocketCount() {
        return socketCount; //
    }

    public void setSocketCount(int t) {
        socketCount = t;
    }

    public httpRegistry getHttpRegistry() {
        return httpRegistry;
    }

    public void send(Transaction notification) {
        for (Transport outboundTransport : outboundTransports)
            if (outboundTransport.hasPath(notification.getDestination()))
                outboundTransport.send(notification);
    }

    public static void setInboundTransports(Transport[] inboundTransport) {
        inboundTransports = inboundTransport;
    }

    public static void setOutboundTransports(Transport[] outboundTransport) {
        outboundTransports = outboundTransport;
    }

    public static Set<Agent> getLocalAgents() {
        return localAgents;
    }

    public static Transport[] getInboundTransports() {
        return inboundTransports;
    }

    public void recv(Transaction notificationDescriptor1) {
        for (Transport inboundTransport : inboundTransports) {
            if (inboundTransport.hasPath(notificationDescriptor1.getDestination())) {
                inboundTransport.recv(notificationDescriptor1);
            } else if (Env.getInstance().isParentHost()) {
                this.send(notificationDescriptor1);
            }
        }
    }

    /**
     * Calls a method on a script object compiled during a previous script execution,
     * which is retained in the state of the <code>ScriptEngine</code>.
     *
     * @param name The name of the procedure to be called.
     * @param thiz If the procedure is a member  of a class
     *             defined in the script and thiz is an instance of that class
     *             returned by a previous execution or invocation, the named method is
     *             called through that instance.
     * @param args Arguments to pass to the procedure.  The rules for converting
     *             the arguments to scripting variables are implementation-specific.
     * @return The value returned by the procedure.  The rules for converting the scripting
     *         variable returned by the script method to a Java Object are implementation-specific.
     * @throws javax.script.ScriptException if an error occurrs during invocation of the method.
     * @throws NoSuchMethodException        if method with given name or matching argument types cannot be found.
     * @throws NullPointerException         if the method name is null.
     * @throws IllegalArgumentException     if the specified thiz is null or the specified Object is
     *                                      does not represent a scripting object.
     */
    public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Used to call top-level procedures and functions defined in scripts.
     *
     * @param args Arguments to pass to the procedure or function
     * @return The value returned by the procedure or function
     * @throws javax.script.ScriptException if an error occurrs during invocation of the method.
     * @throws NoSuchMethodException        if method with given name or matching argument types cannot be found.
     * @throws NullPointerException         if method name is null.
     */
    public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns an implementation of an interface using functions compiled in
     * the interpreter. The methods of the interface
     * may be implemented using the <code>invokeFunction</code> method.
     *
     * @param clasz The <code>Class</code> object of the interface to return.
     * @return An instance of requested interface - null if the requested interface is unavailable,
     *         i. e. if compiled functions in the <code>ScriptEngine</code> cannot be found matching
     *         the ones in the requested interface.
     * @throws IllegalArgumentException if the specified <code>Class</code> object
     *                                  is null or is not an interface.
     */
    public <T> T getInterface(Class<T> clasz) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns an implementation of an interface using member functions of
     * a scripting object compiled in the interpreter. The methods of the
     * interface may be implemented using the <code>invokeMethod</code> method.
     *
     * @param thiz  The scripting object whose member functions are used to implement the methods of the interface.
     * @param clasz The <code>Class</code> object of the interface to return.
     * @return An instance of requested interface - null if the requested interface is unavailable,
     *         i. e. if compiled methods in the <code>ScriptEngine</code> cannot be found matching
     *         the ones in the requested interface.
     * @throws IllegalArgumentException if the specified <code>Class</code> object
     *                                  is null or is not an interface, or if the specified Object is
     *                                  null or does not represent a scripting object.
     */
    public <T> T getInterface(Object thiz, Class<T> clasz) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    enum ProtocolParam {
        Threads("Number of Threads to service all of protocol's ports"),
        Port("Network port number"),
        HostAddress("Host address to use"),
        HostInterface("Host interface to use"),
        Sockets("Multiple dynamic sockets for high load");
        private String description;

        ProtocolParam(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * needs work being friendlier
     *
     * @param arguments usu. the commandline args or the source args for a clone instance
     * @return the props from the commandline
     */
    public Iterable<Map.Entry<CharSequence, Object>> parseCommandLineArgs(String[] arguments) {
        try {
            ArrayList<Entry<CharSequence, Object>> bootMessage = new ArrayList<Entry<CharSequence, Object>>();
            //harsh but effective, asume everything is key value pairs.
            for (int i = 0; i < (arguments.length - (arguments.length % 2)); i += 2) {

                String argument;
                argument = arguments[i];

                if (!argument.startsWith("-")) {
                    throw new RuntimeException("err:parameter '" + argument + "':Params must all start with -");
                }
                String protoToken = argument.substring(1);
                String valueString = arguments[i + 1];
                if (protoToken.equals("help")) {
                    throw new RuntimeException("requested help");
                }
                if (protoToken.equals("name")) {
                    protoToken = FROM_KEY;
                    continue;
                }

                //intercept a few Env specific keywords...
                if (protoToken.equals("HostAddress")) {
                    setHostAddress(InetAddress.getByName(valueString));
                    continue;
                }
                if (protoToken.equals("HostInterface")) {
                    setHostInterface(NetworkInterface.getByName(valueString));
                    continue;
                }

                String[] strings = protoToken.split(":", 2);
                if (strings.length == 2) {
                    try {
                        Transport transport = TransportEnum.valueOf(protoToken);
                        String attrToken = strings[1];

                        ProtocolParam param = ProtocolParam.valueOf(attrToken);

                        switch (param) {
                            case HostAddress:
                                transport.setHostAddress(InetAddress.getByName(valueString));
                                break;
                            case HostInterface:
                                transport.setHostInterface(NetworkInterface.getByName(valueString));
                                break;
                            case Port:
                                transport.setPort(Short.valueOf(valueString));
                                break;
                            case Sockets:
                                transport.setSockets(Integer.valueOf(valueString));
                                break;
                            case Threads:
                                transport.setThreads(Integer.valueOf(valueString));
                                break;
                        }
                    }
                    catch (IllegalArgumentException e) {
                    }
                }
                if (protoToken.equals("HostThreads")) {
                    setHostThreads(Integer.decode(valueString));
                    continue;
                }
                if (protoToken.equals("SocketCount")) {
                    setSocketCount(Integer.decode(valueString));
                    continue;
                }
                if (protoToken.equals("ParentURL")) {
                    Notification evt = (Notification) getParentNode();
                    evt.put("URL", valueString);
                    setParentNode(evt);
                    continue;
                }

                if (protoToken.equals("config")) {
                    Enumeration streamTokenizer = new StringTokenizer(valueString);

                    while (streamTokenizer.hasMoreElements()) {
                        String tempString = (String) streamTokenizer.nextElement();
                        InputStream fileInputStream = new FileInputStream(tempString);
//                        bootMessage.load(fileInputStream);
                        throw new UnsupportedClassVersionError();
                    }
                    continue;
                }
                bootMessage.add(new AbstractMap.SimpleEntry<CharSequence, Object>(protoToken, valueString));
            }
            return bootMessage;
        } catch (RuntimeException e) {
            e.printStackTrace();
            cmdLineHelp("<this was an Env-cmdline syntax problem>");
        } catch (Exception e) {
            e.printStackTrace();  //!TODO: review for fit
        }
        return null;
    }


    public static void cmdLineHelp(String t) {
        String s = "**********" + "***owch kernel Env (global) cmdline options" + "***********\n" +
                "All cmdline params are of the pairs form -key 'Value'\n\n " +
                "valid environmental cmdline options are typically:\n" +
                "-config      - config file[s] to use having (RFC822) pairs of Key: Value\n" +
                "-JMSReplyTo  - Name of agent\n" +
                "-name        - shorthand for JMSReplyTo\n" +
                "-HostAddress - Host address to use\n" +
                "-HostInterface - Host interface to use\n" +
                "-SocketCount - Multiple dynamic sockets for high load?\n" +
                "-debugLevel  - controls how much scroll is displayed\n";
        s += "-ParentURL   - typically owch://hostname:2112 -- instructs our agent host where to find an uplink\n\n";
        s += "this edition of the Agent Hosting Platform comes with the folowing configurable protocols: \n";
        for (TransportEnum ptype : TransportEnum.values()) {
            if (ptype.getPort() == -1) {
                continue;
            }
            s += "\t" + ptype.toString();
        }
        s += "\n\n\t -- Each protocol allows the following configurable syntax: \n";
        for (ProtocolParam param : ProtocolParam.values()) {
            s += "[-<proto>:" + param.name() + "]\t-\t" + param.getDescription() + "\n";
        }

        s = s + "\n\n\tthis Edition of the parser: $Id$\n\n\n" +
                "**********" + "*** Agent Env cmdline specification:" + "***********\n" + t;
        System.out.println(s);

        System.exit(2);
    }


    public void sethttpRegistry(httpRegistry h) {
    }

    public Format getFormat(Object name) {
        return getFormatCache().get(name);
    }

    public void registerFormat(String name, Format f) {
        getFormatCache().put(name, f);
        //  if (Env.logDebug) Env.log(100, "Registering Formatter: " + name);
    }

    private Map<String, Format> getFormatCache() {
        if (formatCache == null) {
            formatCache = new TreeMap<String, Format>();
        }
        return formatCache;
    }

    public URI getDefaultURI() {
        if (!isParentHost()) {
            return getParentNode().getURI();
        } else {
            return null;
        }
    }


    /**
     * sets the flag on the Factory Objects to act as parental sendr in all  location resolution.
     *
     * @param flag sets the state to true or false
     */
    public void setParentHost(boolean flag) {
        parentFlag = flag;
    }

    public void setParentNode(Notification l) {
        parentNode = l;
    }

    /**
     * accessor for parental node being present in the current Process.
     *
     * @return whether we are the Parent Sendr of all transactions
     */
    public boolean isParentHost() {
        return parentFlag; //
    }


    public HasOrigin getParentNode() {
        if (parentNode == null) {
            DefaultMapNotification l = new DefaultMapNotification();
            l.put("Created", "env.getDomain()");
            l.put(FROM_KEY, "default");
            l.put(URI_KEY, "owch://" + getHostAddress().getCanonicalHostName() + ":2112/");
            parentNode = l;
        }
        return parentNode;
    }


    public void setDomainName(String dName) {
        domainName = dName;
    }

    public InetAddress getHostAddress() {
        if (null == hostAddress) {
            NetworkInterface hostInterface;
            hostInterface = getHostInterface();
            if (hostInterface != null)
                return hostAddress = getExternalAddress(hostInterface);


            Enumeration<NetworkInterface> networkInterfaces = null;


            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                return null;
            }
            while (networkInterfaces.hasMoreElements() && hostAddress == null) {
                hostInterface = networkInterfaces.nextElement();
                setHostInterface(hostInterface);
//                Env.log(133, "Interface name: " + hostInterface.getName());
//                Env.log(133, "Interface DisplayName: " + hostInterface.getDisplayName());
                hostAddress = getExternalAddress(hostInterface);
            }
        }
        return hostAddress;
    }

    private static InetAddress getExternalAddress(NetworkInterface hostInterface) {
        InetAddress siteLocalAddress;
        siteLocalAddress = null;
        Enumeration<InetAddress> inetAddresses;

        if (hostInterface != null) {
            inetAddresses = hostInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddr = inetAddresses.nextElement();
                if (inetAddr.isSiteLocalAddress()) {
                    siteLocalAddress = inetAddr;
                }
                if (!inetAddr.isAnyLocalAddress() && !inetAddr.isLinkLocalAddress() && !inetAddr.isLoopbackAddress() && !inetAddr.isMulticastAddress() && !inetAddr.isSiteLocalAddress())
                    return inetAddr;
            }
        }
        return siteLocalAddress;
    }


    private void setHostAddress(InetAddress s333s) {
        hostAddress = s333s;
    }

    public String getHostname() {
        return getHostAddress().getHostName();
    }

    public String getDomainName() {
        return domainName;
    }


    public void setHostInterface(NetworkInterface a2) {
        hostInterface = a2;
    }

    public NetworkInterface getHostInterface() {
        return hostInterface;
    }

    public static Env getInstance() {
        synchronized (Env.class) {
            if (null == instance)
                instance = new Env();
            return instance;
        }
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getPipePort() {
        return pipePort;
    }

    public void setPipePort(int pipePort) {
        this.pipePort = pipePort;
    }

    static {


    }


}


