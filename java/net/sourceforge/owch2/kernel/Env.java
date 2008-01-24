package net.sourceforge.owch2.kernel;

import net.sourceforge.owch2.router.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Env class: mobile agent host environment; <P>intent: the master object factory
 * <P>Summary: This holds quite a few package-local and global variables and accessors.
 *
 * @author James Northrup
 * @version $Id: Env.java,v 1.4 2005/06/04 02:26:23 grrrrr Exp $
 */
public class Env {
    public boolean shutdown = false;
    private boolean parentFlag = false;
    private int owchPort = 0;
    private int httpPort = 0;
    private int hostThreads = 2;
    private int socketCount = 2;
    private String domainName = null;
    private Map<ProtocolType, Router> routerCache = new HashMap<ProtocolType, Router>(13);
    private Map<String, Format> formatCache;


    /**
     * returns a MetaProperties suitable for parent routing.
     */
    MetaAgent parentNode = null;


    private RouteHunter routeHunter = new LeafRouteHunter();

    private InetAddress hostAddress;
    private NetworkInterface hostInterface;

    private static Env instance;
    public boolean logDebug = false;
    private int pipePort;

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

    public void send(Map item) {
        routeHunter.send(item); //
    }

    public void unRoute(Object key) {
        routeHunter.remove(key); //
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
                String protoToken = argument.substring(1);
                String valueString = arguments[i + 1];
                if (protoToken.equals("help")) {
                    throw new RuntimeException("requested help");
                }
                if (protoToken.equals("name")) {
                    protoToken = Notification.REPLYTO_KEY;
                }

                //intercept a few Env specific keywords...
                if (protoToken.equals("HostAddress")) {
                    setHostAddress(InetAddress.getByName(valueString));
                }
                if (protoToken.equals("HostInterface")) {
                    setHostInterface(NetworkInterface.getByName(valueString));

                }

                String[] strings = protoToken.split(":", 2);
                if (strings.length == 2) {
                    try {
                        ProtocolType ptype = ProtocolType.valueOf(protoToken);
                        String attrToken = strings[1];

                        ProtocolParam param = ProtocolParam .valueOf(attrToken);

                        switch (param) {
                            case HostAddress:
                                ptype.setHostAddress(InetAddress.getByName(valueString));
                                break;
                            case HostInterface:
                                ptype.setHostInterface(NetworkInterface.getByName(valueString));
                                break;
                            case Port:
                                ptype.setDefaultPort(Integer.valueOf(valueString));
                                break;
                            case Sockets:
                                ptype.setSocketCount(Integer.valueOf(valueString));
                                break;
                            case Threads:
                                ptype.setThreads(Integer.valueOf(valueString));
                                break;
                        }
                    }
                    catch (IllegalArgumentException e) {
                    }
                }
                if (protoToken.equals("HostThreads")) {
                    setHostThreads(Integer.decode(valueString).intValue());
                }
                if (protoToken.equals("SocketCount")) {
                    setSocketCount(Integer.decode(valueString).intValue());
                }
                if (protoToken.equals("ParentURL")) {
                    Location location = (Location) getParentNode();
                    location.put("URL", valueString);
                    setParentNode(location);
                    continue;
                }

                if (protoToken.equals("config")) {
                    StringTokenizer streamTokenizer = new StringTokenizer(valueString);

                    while (streamTokenizer.hasMoreElements()) {
                        String tempString = (String) streamTokenizer.nextElement();
                        InputStream fileInputStream = new FileInputStream(tempString);
                        bootNotification.load(fileInputStream);
                    }
                    continue;
                }
                bootNotification.put(protoToken, valueString);
            }
            return bootNotification;
        } catch (RuntimeException e) {
            e.printStackTrace();
            cmdLineHelp("<this was an Env-cmdline syntax problem>");
        } catch (SocketException e) {
            e.printStackTrace();  //!TODO: review for fit
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //!TODO: review for fit
        } catch (UnknownHostException e) {
            e.printStackTrace();  //!TODO: review for fit
        } catch (IOException e) {
            e.printStackTrace();  //!TODO: review for fit
        }
        return null;
    }


    public void cmdLineHelp(String t) {
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
        for (ProtocolType ptype : ProtocolType.values()) {
            if (ptype.getDefaultPort() == null) {
                continue;
            }
            s += "\t" + ptype.toString();
        }
        ;
        s += "\n\n\t -- Each protocol allows the following configurable syntax: \n";
        for (ProtocolParam param : ProtocolParam.values()) {
            s += "[-<proto>:" + param.name() + "]\t-\t" + param.getDescription() + "\n";
        }

        s = s + "\n\n\tthis Edition of the parser: $Id: Env.java,v 1.4 2005/06/04 02:26:23 grrrrr Exp $\n\n\n" +
                "**********" + "*** Agent Env cmdline specification:" + "***********\n" + t;
        System.out.println(s);

        System.exit(2);
    }


    public void sethttpRegistry(httpRegistry h) {
    }

    public Format getFormat(String name) {
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
                    return (inetAddr);
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
        if (null == instance) instance = new Env();
        return instance;
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

}


