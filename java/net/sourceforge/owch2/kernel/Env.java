package net.sourceforge.owch2.kernel;

import net.sourceforge.owch2.router.*;

import java.io.*;
import java.net.*;
import java.util.*;

import org.omg.CORBA.portable.UnknownException;

/**
 * Env class: mobile agent host environment; <P>intent: the master object factory
 * <P>Summary: This holds quite a few package-local and global variables and accessors.
 * @version $Id: Env.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public final class Env extends Log {
    public static boolean shutdown = false;
    private static boolean parentFlag = false;
    private static int hostPort = 0;
    private static int hostThreads = 2;
    private static int socketCount = 2;
    private static String domainName = null;
    private static Map routerCache = new HashMap(13);
    private static httpFactory httpFactory;
    private static httpRegistry webRegistry;
    private static Map formatCache;
    private static NotificationFactory notificationFactory;
    private static ProtocolCache protocolCache;
    private static owchDispatch datagramDispatch;
    private static owchFactory socketFactory;
    /** returns a MetaProperties suitable for parent routing. */
    static MetaAgent parentNode = null;

    private static RouteHunter routeHunter = new LeafRouteHunter();

    private static InetAddress hostAddress;
    static private NetworkInterface hostInterface;


    public static final int getHostPort() {
        return hostPort;
    };

    public static final void setHostPort(int port) {
        hostPort = port;
    };

    public static final int getHostThreads() {
        return hostThreads; //
    };

    public static final void setHostThreads(int t) {
        hostThreads = t;
    };

    public static final int getSocketCount() {
        return socketCount; //
    };

    public static final void setSocketCount(int t) {
        socketCount = t;
    };

    public final static void send(Map item) {
        routeHunter.send(item); //
    };

    public final static void unRoute(Object key) {
        routeHunter.remove(key); //
    };

    public final static Router getRouter(Object key) {
        String className;
        className = Router.class.getPackage().getName() + "." + key + "Router";
        if (Env.logDebug) Env.log(500, "attempting to pull up router " + className);
        Router theRouter;
        theRouter = (Router) routerCache.get(key);
        if (theRouter == null) {
            try {
                theRouter = (Router) Class.forName(className).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            routerCache.put(key, theRouter);
        }
        return theRouter;
    };

    /** needs work being friendlier */
    static public Map parseCommandLineArgs(String[] arguments) {
        try {
            Notification bootNotification = new Notification();
            //harsh but effective, asume everything is key value pairs.
            for (int i = 0; i < (arguments.length - arguments.length % 2); i += 2) {

                String argument;
                argument = arguments[i];

                if (!argument.startsWith("-")) {
                    throw new RuntimeException("err:parameter '" + argument + "':Params must all start with -");
                }
                String key = argument.substring(1);
                String valueString = arguments[i + 1];
                if (key.equals("help")) {
                    throw new RuntimeException("requested help");
                }
                if (key.equals("name")) {
                    key = "JMSReplyTo";
                }

                //intercept a few Env specific keywords...
                if (key.equals("HostAddress")) {
                    setHostAddress(InetAddress.getByName(valueString));
                }
                if (key.equals("HostInterface")) {
                    setHostInterface(NetworkInterface.getByName(valueString));

                }
                if (key.equals("debugLevel")) {
                    setDebugLevel(Integer.decode(valueString).intValue());
                }
                if (key.equals("HostPort")) {
                    setHostPort(Integer.decode(valueString).intValue());
                }
                if (key.equals("HostThreads")) {
                    setHostThreads(Integer.decode(valueString).intValue());
                }
                if (key.equals("SocketCount")) {
                    setSocketCount(Integer.decode(valueString).intValue());
                }
                if (key.equals("ParentURL")) {
                    Location location = (Location) Env.getParentNode();
                    location.put("URL", valueString);
                    setParentNode(location);
                    continue;
                }

                if (key.equals("config")) {
                    StringTokenizer streamTokenizer = new StringTokenizer(valueString);

                    while (streamTokenizer.hasMoreElements()) {
                        String tempString = (String) streamTokenizer.nextElement();
                        InputStream fileInputStream = new FileInputStream(tempString);
                        bootNotification.load(fileInputStream);
                    }
                    continue;
                }
                bootNotification.put(key, valueString);
            }
            return bootNotification;
        } catch (RuntimeException e) {
            e.printStackTrace();
            cmdLineHelp("<this was an Env-cmdline syntax problem>");
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
        return null;
    }

    public static final void cmdLineHelp(String t) {
        System.out.println("**********" + "***owch kernel Env (global) cmdline options" + "***********\n" +
                "All cmdline params are of the pairs form -key 'Value'\n\n " +
                "valid environmental cmdline options are typically:\n" +
                "-config      - config file[s] to use having (RFC822) pairs of Key: Value\n" +
                "-JMSReplyTo  - Name of agent\n" +
                "-name        - shorthand for JMSReplyTo\n" +
                "-HostPort    - port number\n" +
                "-HostThreads - Host Thread count \n" +
                "-HostAddress - Host address to use\n" +
                "-HostInterface - Host interface to use\n" +
                "-SocketCount - Multiple dynamic sockets for high load?\n" +
                "-debugLevel  - controls how much scroll is displayed\n" +
                "-ParentURL   - typically owch://hostname:2112 -- instructs our agent host where to find an uplink\n\n" +
                " this Edition of the parser: $Id: Env.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $\n\n\n" +
                "**********" + "*** Agent Env cmdline specification:" + "***********\n" + t);

        System.exit(2);
    };


    static public void sethttpRegistry(httpRegistry h) {
        webRegistry = h;
    };

    static public httpRegistry gethttpRegistry() {
        if (webRegistry == null) {
            webRegistry = new httpRegistry();
        }
        return webRegistry;
    };



    public final static Format getFormat(String name) {
        return (Format) getFormatCache().get(name);
    };

    public final static void registerFormat(String name, Format f) {
        getFormatCache().put(name, f);
        if (Env.logDebug) Env.log(100, "Registering Formatter: " + name);
    };

    private final static Map getFormatCache() {
        if (formatCache == null) {
            formatCache = new TreeMap();
        }
        return formatCache;
    };

    public final static URLString getDefaultURL() {
        if (!isParentHost()) {
            return new URLString(Env.getParentNode().getURL());
        } else {
            return null;
        }
    }

    public final static ProtocolCache getProtocolCache() {
        if (protocolCache == null) {
            protocolCache = new ProtocolCache();
        }
        return protocolCache;
    };

    public final static MetaProperties getLocation(String Protocol) {
        if (Env.logDebug) Env.log(50, "Env.getLocation - " + Protocol);
        MetaProperties l = getProtocolCache().getLocation(Protocol);
        return l;
    };

    /**
     * sets the flag on the Factory Objects to act as parental sendr in all final location resolution.
     * @param flag sets the state to true or false
     */
    final static public void setParentHost(boolean flag) {
        parentFlag = flag;
    }

    final static public void setParentNode(MetaAgent l) {
        parentNode = l;
    }

    /**
     * accessor for parental node being present in the current Process.
     * @return whether we are the Parent Sendr of all transactions
     */
    public final static boolean isParentHost() {
        return parentFlag; //
    }

    /**
     * Sets the Parent AbstractAgent info Object.
     * @param s a MetaProperties
     */
    static final void setowchDispatch(owchDispatch s) {
        datagramDispatch = s;
    };

    static final void setNotificationFactory(NotificationFactory s) {
        notificationFactory = s;
    };

    /**
     * sets the process's ServerSocket provider Env.
     * @param s New SocketEnv.
     */
    public static final void setowchFactory(owchFactory s) {
        socketFactory = s;
    }

    public final static MetaAgent getParentNode() {
        if (parentNode == null) {
            Location l = new Location();
            l.put("Created", "env.getDomain()");
            l.put("JMSReplyTo", "default");
            l.put("URL", "owch://" + getHostAddress().getCanonicalHostName() + ":2112/");
            parentNode = l;
        }
        return parentNode;
    }

    public final static owchDispatch getowchDispatch() {
        if (datagramDispatch == null) {
            datagramDispatch = new owchDispatch();
        }
        return datagramDispatch;
    };

    final static NotificationFactory getNotificationFactory() {
        if (notificationFactory == null) {
            try {
                notificationFactory = new NotificationFactory();
            } catch (SocketException e) {
                throw new Error(e.toString());
            }
        }
        return notificationFactory;
    };

    final public static owchFactory getowchFactory() {
        if (socketFactory == null) {
            socketFactory = new owchFactory();
        }
        return socketFactory;
    }

    final public static httpFactory gethttpFactory() {
        if (httpFactory == null) {
            httpFactory = new httpFactory();
        }
        return httpFactory;
    }


    final public static void setDomainName(String dName) {
        domainName = dName;
    }

    public static RouteHunter getRouteHunter() {
        return routeHunter;
    }

    static public void setRouteHunter(RouteHunter r) {
        routeHunter = r;
    }


    public static InetAddress getHostAddress() {
        if (null == hostAddress) {
            NetworkInterface hostInterface;
            hostInterface = getHostInterface();
            if (hostInterface != null)
                return hostAddress = getExternalAddress(hostInterface);


            Enumeration networkInterfaces = null;


            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                return null;
            }
            while (networkInterfaces.hasMoreElements() && hostAddress == null) {
                hostInterface = (NetworkInterface) networkInterfaces.nextElement();
                setHostInterface(hostInterface);
                Env.log(133, "Interface name: " + hostInterface.getName());
                Env.log(133, "Interface DisplayName: " + hostInterface.getDisplayName());
                hostAddress = getExternalAddress(hostInterface);
            }
        }
        return hostAddress;
    }

    private static InetAddress getExternalAddress(NetworkInterface hostInterface) {
        InetAddress linkLocalAddress;
        linkLocalAddress = null;
        InetAddress siteLocalAddress;
        siteLocalAddress = null;
        InetAddress loopLocal;
        loopLocal = null;
        Enumeration inetAddresses;

        if (hostInterface != null) {
            inetAddresses = hostInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddr = null;
                inetAddr = (InetAddress) inetAddresses.nextElement();
                if (inetAddr.isSiteLocalAddress()) {
                    siteLocalAddress = inetAddr;
                }
//                if (inetAddr.isLoopbackAddress()) loopLocal = inetAddr;
//                if (inetAddr.isLinkLocalAddress()) linkLocalAddress = inetAddr;
                if (isExternalAddress(inetAddr))
                    return (inetAddr);
            }
        }
       return siteLocalAddress;
//        if (null != linkLocalAddress) return linkLocalAddress;
//        if (null != loopLocal) return loopLocal;
//        try {
//            return Inet4Address.getByName("0.0.0.0");
//        } catch (UnknownHostException e) {
//            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
//        }
    }                                          ;


    private static boolean isExternalAddress(InetAddress inetAddr) {
        Env.log(133, "addr hostname: " + inetAddr.getHostName());
        Env.log(133, "addr cannonical hostname: " + inetAddr.getCanonicalHostName());
        boolean anyLocalAddress = inetAddr.isAnyLocalAddress();
        Env.log(133, "addr isAnyLocalAddress " + anyLocalAddress);
        boolean linkLocalAddress = inetAddr.isLinkLocalAddress();
        Env.log(133, "addr isLinkLocalAddress " + linkLocalAddress);
        boolean loopbackAddress = inetAddr.isLoopbackAddress();
        Env.log(133, "addr isLoopbackAddress " + loopbackAddress);
        boolean multicastAddress = inetAddr.isMulticastAddress();
        Env.log(133, "addr isMulticastAddress " + multicastAddress);
        boolean siteLocalAddress = inetAddr.isSiteLocalAddress();
        Env.log(133, "addr isSiteLocalAddress " + siteLocalAddress);

        if (anyLocalAddress || linkLocalAddress || loopbackAddress || multicastAddress || siteLocalAddress) return false;
        return true;
    }

    private static void setHostAddress(InetAddress s333s) {
        hostAddress = s333s;
    }

    public static String getHostname() {
        return getHostAddress().getHostName();
    }

    public static String getDomainName() {
        return domainName;
    }

    static public NetworkInterface getHostInterface() {
        return hostInterface;
    }

    static public void setHostInterface(NetworkInterface a2) {
        hostInterface = a2;
    }
}


