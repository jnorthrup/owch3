package net.sourceforge.owch2.kernel;

import net.sourceforge.owch2.router.LeafRouteHunter;
import net.sourceforge.owch2.router.RouteHunter;
import net.sourceforge.owch2.router.Router;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Env class: mobile agent host environment; <P>intent: the master object factory
 * <P>Summary: This holds quite a few package-local and global variables and accessors.
 *
 * @author James Northrup
 * @version $Id: Env.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class Env {
    public boolean shutdown = false;
    private boolean parentFlag = false;
    private int hostPort = 0;
    private int hostThreads = 2;
    private int socketCount = 2;
    private String domainName = null;
    private Map<String, Router> routerCache = new HashMap<String, Router>(13);
    private httpFactory httpFactory;
    private httpRegistry webRegistry;
    private Map<String, Format> formatCache;
    private NotificationFactory notificationFactory;
    private ProtocolCache protocolCache;
    private owchDispatch datagramDispatch;
    private owchFactory socketFactory;

    /**
     * returns a MetaProperties suitable for parent routing.
     */
    MetaAgent parentNode = null;


    private RouteHunter routeHunter = new LeafRouteHunter();

    private InetAddress hostAddress;
    private NetworkInterface hostInterface;

    private static Env instance;
    public boolean logDebug = false;

    private Env() {
    }


    public int getHostPort() {
        return hostPort;
    }

    ;

    public void setHostPort(int port) {
        hostPort = port;
    }

    ;

    public int getHostThreads() {
        return hostThreads; //
    }

    ;

    public void setHostThreads(int t) {
        hostThreads = t;
    }

    ;

    public int getSocketCount() {
        return socketCount; //
    }

    ;

    public void setSocketCount(int t) {
        socketCount = t;
    }

    ;

    public void send(Map item) {
        routeHunter.send(item); //
    }

    ;

    public void unRoute(Object key) {
        routeHunter.remove(key); //
    }

    ;

    public Router getRouter(String key) {
        String className;
        className = Router.class.getPackage().getName() + "." + key + "Router";
        Router theRouter;
        theRouter = routerCache.get(key);
        if (theRouter == null) {
            try {
                theRouter = (Router) Class.forName(className).newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            routerCache.put(key, theRouter);
        }
        return theRouter;
    }

    ;

    /**
     * needs work being friendlier
     */
    public Map parseCommandLineArgs(String[] arguments) {
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
//                if (key.equals("debugLevel")) {
//
//                    setDebugLevel(Integer.decode(valueString).intValue());
//                }
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
                    Location location = (Location) getParentNode();
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


    public void cmdLineHelp(String t) {
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
                " this Edition of the parser: $Id: Env.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $\n\n\n" +
                "**********" + "*** Agent Env cmdline specification:" + "***********\n" + t);

        System.exit(2);
    }

    ;


    public void sethttpRegistry(httpRegistry h) {
        webRegistry = h;
    }

    ;

    public httpRegistry gethttpRegistry() {
        if (webRegistry == null) {
            webRegistry = new httpRegistry();
        }
        return webRegistry;
    }

    ;


    public Format getFormat(String name) {
        return getFormatCache().get(name);
    }

    ;

    public void registerFormat(String name, Format f) {
        getFormatCache().put(name, f);
        //  if (Env.logDebug) Env.log(100, "Registering Formatter: " + name);
    }

    ;

    private Map<String, Format> getFormatCache() {
        if (formatCache == null) {
            formatCache = new TreeMap<String, Format>();
        }
        return formatCache;
    }

    ;

    public URLString getDefaultURL() {
        if (!isParentHost()) {
            return new URLString(getParentNode().getURL());
        } else {
            return null;
        }
    }

    public ProtocolCache getProtocolCache() {
        if (protocolCache == null) {
            protocolCache = new ProtocolCache();
        }
        return protocolCache;
    }

    ;

    public MetaProperties getLocation(String Protocol) {
        //if (Env.logDebug) Env.log(50, "Env.getLocation - " + Protocol);
        MetaProperties l = getProtocolCache().getLocation(Protocol);
        return l;
    }

    ;

    /**
     * sets the flag on the Factory Objects to act as parental sendr in all  location resolution.
     *
     * @param flag sets the state to true or false
     */
    public void setParentHost(boolean flag) {
        parentFlag = flag;
    }

    public void setParentNode(MetaAgent l) {
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

    /**
     * Sets the Parent AbstractAgent info Object.
     *
     * @param s a MetaProperties
     */
    void setowchDispatch(owchDispatch s) {
        datagramDispatch = s;
    }

    ;

    void setNotificationFactory(NotificationFactory s) {
        notificationFactory = s;
    }

    ;

    /**
     * sets the process's ServerSocket provider Env.
     *
     * @param s New SocketEnv.
     */
    public void setowchFactory(owchFactory s) {
        socketFactory = s;
    }

    public MetaAgent getParentNode() {
        if (parentNode == null) {
            Location l = new Location();
            l.put("Created", "env.getDomain()");
            l.put("JMSReplyTo", "default");
            l.put("URL", "owch://" + getHostAddress().getCanonicalHostName() + ":2112/");
            parentNode = l;
        }
        return parentNode;
    }

    public owchDispatch getowchDispatch() {
        if (datagramDispatch == null) {
            datagramDispatch = new owchDispatch();
        }
        return datagramDispatch;
    }

    ;

    NotificationFactory getNotificationFactory() {
        if (notificationFactory == null) {
            try {
                notificationFactory = new NotificationFactory();
            } catch (SocketException e) {
                throw new Error(e.toString());
            }
        }
        return notificationFactory;
    }

    ;

    public owchFactory getowchFactory() {
        if (socketFactory == null) {
            socketFactory = new owchFactory();
        }
        return socketFactory;
    }

    public httpFactory gethttpFactory() {
        if (httpFactory == null) {
            httpFactory = new httpFactory();
        }
        return httpFactory;
    }


    public void setDomainName(String dName) {
        domainName = dName;
    }

    public RouteHunter getRouteHunter() {
        return routeHunter;
    }

    public void setRouteHunter(RouteHunter r) {
        routeHunter = r;
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

    private InetAddress getExternalAddress(NetworkInterface hostInterface) {
        InetAddress linkLocalAddress;
        linkLocalAddress = null;
        InetAddress siteLocalAddress;
        siteLocalAddress = null;
        InetAddress loopLocal;
        loopLocal = null;
        Enumeration<InetAddress> inetAddresses;

        if (hostInterface != null) {
            inetAddresses = hostInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddr = null;
                inetAddr = inetAddresses.nextElement();
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
    }

    ;


    private boolean isExternalAddress(InetAddress inetAddr) {
        boolean anyLocalAddress = inetAddr.isAnyLocalAddress();
        boolean linkLocalAddress = inetAddr.isLinkLocalAddress();
//        Env.log(133, "addr isLinkLocalAddress " + linkLocalAddress);
        boolean loopbackAddress = inetAddr.isLoopbackAddress();
//        Env.log(133, "addr isLoopbackAddress " + loopbackAddress);
        boolean multicastAddress = inetAddr.isMulticastAddress();
//        Env.log(133, "addr isMulticastAddress " + multicastAddress);
        boolean siteLocalAddress = inetAddr.isSiteLocalAddress();
//        Env.log(133, "addr isSiteLocalAddress " + siteLocalAddress);

        if (anyLocalAddress || linkLocalAddress || loopbackAddress || multicastAddress || siteLocalAddress)
            return false;
        return true;
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
        if (null == instance) instance = new Env();
        return instance;
    }

    public void log(int i, String s) {

    }

}


