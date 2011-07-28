package net.sourceforge.owch2.kernel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import net.sourceforge.owch2.router.LeafRouteHunter;
import net.sourceforge.owch2.router.RouteHunter;
import net.sourceforge.owch2.router.Router;

public final class Env extends Log {
  public static boolean shutdown = false;
  private static boolean parentFlag = false;
  private static int hostPort = 0;
  private static int hostThreads = 2;
  private static int socketCount = 2;
  private static String domainName = null;
  private static Map<Object, Router> routerCache = new HashMap<Object, Router>(13);
  private static httpFactory httpFactory;
  private static httpRegistry webRegistry;
  private static Map<String, Format> formatCache;
  private static NotificationFactory notificationFactory;
  private static ProtocolCache protocolCache;
  private static owchDispatch datagramDispatch;
  private static owchFactory socketFactory;
  static MetaAgent parentNode = null;

  private static RouteHunter routeHunter = new LeafRouteHunter();
  private static InetAddress hostAddress;
  private static NetworkInterface hostInterface;

  public static int getHostPort() {
    return hostPort;
  }

  public static void setHostPort(int port) {
    hostPort = port;
  }

  public static int getHostThreads() {
    return hostThreads;
  }

  public static void setHostThreads(int t) {
    hostThreads = t;
  }

  public static int getSocketCount() {
    return socketCount;
  }

  public static void setSocketCount(int t) {
    socketCount = t;
  }

  public static void send(Map item) {
    routeHunter.send(item);
  }

  public static void unRoute(Object key) {
    routeHunter.remove(key);
  }

  public static Router getRouter(Object key) {
    String className = Router.class.getPackage().getName() + "." + key + "Router";
    log(500, "attempting to pull up router " + className);

    Router theRouter = routerCache.get(key);
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

  public static Map parseCommandLineArgs(String[] arguments) {
    try {
      Notification bootNotification = new Notification();

      for (int i = 0; i < arguments.length - arguments.length % 2; i += 2) {
        String argument = arguments[i];

        if (!argument.startsWith("-")) {
          throw new RuntimeException("err:parameter '" + argument + "':Params must all start with -");
        }
        String key = argument.substring(1);
        String valueString = arguments[(i + 1)];
        if (key.equals("help")) {
          throw new RuntimeException("requested help");
        }
        if (key.equals("name")) {
          key = "JMSReplyTo";
        }

        if (key.equals("HostAddress")) {
          setHostAddress(InetAddress.getByName(valueString));
        }
        if (key.equals("HostInterface")) {
          setHostInterface(NetworkInterface.getByName(valueString));
        }

        if (key.equals("debugLevel")) {
          Log.setDebugLevel(Integer.decode(valueString));
        }
        if (key.equals("HostPort")) {
          setHostPort(Integer.decode(valueString));
        }
        if (key.equals("HostThreads")) {
          setHostThreads(Integer.decode(valueString));
        }
        if (key.equals("SocketCount")) {
          setSocketCount(Integer.decode(valueString));
        }
        if (key.equals("ParentURL")) {
          Location location = (Location) getParentNode();
          location.put("URL", valueString);
          setParentNode(location);
        } else if (key.equals("config")) {
          StringTokenizer streamTokenizer = new StringTokenizer(valueString);

          while (streamTokenizer.hasMoreElements()) {
            String tempString = (String) streamTokenizer.nextElement();
            InputStream fileInputStream = new FileInputStream(tempString);
            bootNotification.load(fileInputStream);
          }
        } else {
          bootNotification.put(key, valueString);
        }
      }
      return bootNotification;
    } catch (RuntimeException e) {
      e.printStackTrace();
      cmdLineHelp("<this was an Env-cmdline syntax problem>");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void cmdLineHelp(String t) {
    System.out.println("*************owch kernel Env (global) cmdline options***********\nAll cmdline params are of the pairs form -key 'Value'\n\n valid environmental cmdline options are typically:\n-config      - config file[s] to use having (RFC822) pairs of Key: Value\n-JMSReplyTo  - Name of agent\n-name        - shorthand for JMSReplyTo\n-HostPort    - port number\n-HostThreads - Host Thread count \n-HostAddress - Host address to use\n-HostInterface - Host interface to use\n-SocketCount - Multiple dynamic sockets for high load?\n-debugLevel  - controls how much scroll is displayed\n-ParentURL   - typically owch://hostname:2112 -- instructs our agent host where to find an uplink\n\n this Edition of the parser: $Id: Env.java,v 1.1.1.1 2002/12/08 16:41:53 jim Exp $\n\n\n************* Agent Env cmdline specification:***********\n" + t);

    System.exit(2);
  }

  public static void sethttpRegistry(httpRegistry h) {
    webRegistry = h;
  }

  public static httpRegistry gethttpRegistry() {
    if (webRegistry == null) {
      webRegistry = new httpRegistry();
    }
    return webRegistry;
  }

  public static Format getFormat(String name) {
    return getFormatCache().get(name);
  }

  public static void registerFormat(String name, Format f) {
    getFormatCache().put(name, f);
    log(100, "Registering Formatter: " + name);
  }

  private static Map<String, Format> getFormatCache() {
    if (formatCache == null) {
      formatCache = new TreeMap<String, Format>();
    }
    return formatCache;
  }

  public static URLString getDefaultURL() {
    if (!isParentHost()) {
      return new URLString(getParentNode().getURL());
    }
    return null;
  }

  public static ProtocolCache getProtocolCache() {
    if (protocolCache == null) {
      protocolCache = new ProtocolCache();
    }
    return protocolCache;
  }

  public static MetaProperties getLocation(String Protocol) {
    log(50, "Env.getLocation - " + Protocol);

    return getProtocolCache().getLocation(Protocol);
  }

  public static void setParentHost(boolean flag) {
    parentFlag = flag;
  }

  public static void setParentNode(MetaAgent l) {
    parentNode = l;
  }

  public static boolean isParentHost() {
    return parentFlag;
  }

  static void setowchDispatch(owchDispatch s) {
    datagramDispatch = s;
  }

  static void setNotificationFactory(NotificationFactory s) {
    notificationFactory = s;
  }

  public static void setowchFactory(owchFactory s) {
    socketFactory = s;
  }

  public static MetaAgent getParentNode() {
    if (parentNode == null) {
      Location l = new Location();
      l.put("Created", "env.getDomain()");
      l.put("JMSReplyTo", "default");
      l.put("URL", "owch://0.0.0.0:2112");
      parentNode = l;
    }
    return parentNode;
  }

  public static owchDispatch getowchDispatch() {
    if (datagramDispatch == null) {
      datagramDispatch = new owchDispatch();
    }
    return datagramDispatch;
  }

  static NotificationFactory getNotificationFactory() {
    if (notificationFactory == null) {
      try {
        notificationFactory = new NotificationFactory();
      } catch (SocketException e) {
        throw new Error(e.toString());
      }
    }
    return notificationFactory;
  }

  public static owchFactory getowchFactory() {
    if (socketFactory == null) {
      socketFactory = new owchFactory();
    }
    return socketFactory;
  }

  public static httpFactory gethttpFactory() {
    if (httpFactory == null) {
      httpFactory = new httpFactory();
    }
    return httpFactory;
  }

  public static void setDomainName(String dName) {
    domainName = dName;
  }

  public static RouteHunter getRouteHunter() {
    return routeHunter;
  }

  public static void setRouteHunter(RouteHunter r) {
    routeHunter = r;
  }

  public static InetAddress getHostAddress() {
    InetAddress r = null;
    do {
      if (null == hostAddress) {
        NetworkInterface hostInterface = getHostInterface();
        if (hostInterface != null) {
          r = Env.hostAddress = getExternalAddress(hostInterface);
          break;
        }

        Enumeration<NetworkInterface> networkInterfaces = null;
        try {
          networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
          e.printStackTrace();
          r = null;
          break;
        }
        while ((networkInterfaces.hasMoreElements()) && (hostAddress == null)) {
          hostInterface = networkInterfaces.nextElement();
          setHostInterface(hostInterface);
          log(133, "Interface name: " + hostInterface.getName());
          log(133, "Interface DisplayName: " + hostInterface.getDisplayName());
          r = hostAddress = getExternalAddress(hostInterface);
          break;
        }
      }
    } while (false);
    try {
      r = r == null ? InetAddress.getLocalHost() : r;
    } catch (UnknownHostException e) {
      e.printStackTrace();  //todo: verify for a purpose
    } finally {
    }
    return r;
  }

  private static InetAddress getExternalAddress(NetworkInterface hostInterface) {
    if (hostInterface != null) {
      Enumeration<InetAddress> inetAddresses = hostInterface.getInetAddresses();
      while (inetAddresses.hasMoreElements()) {
        InetAddress inetAddr = inetAddresses.nextElement();
        boolean externalAddress = isExternalAddress(inetAddr);

        if (externalAddress)
          return inetAddr;
      }
    }
    return null;
  }

  private static boolean isExternalAddress(InetAddress inetAddr) {
    log(133, "addr hostname: " + inetAddr.getHostName());
    log(133, "addr cannonical hostname: " + inetAddr.getCanonicalHostName());
    boolean anyLocalAddress = inetAddr.isAnyLocalAddress();
    log(133, "addr isAnyLocalAddress " + anyLocalAddress);
    boolean linkLocalAddress = inetAddr.isLinkLocalAddress();
    log(133, "addr isLinkLocalAddress " + linkLocalAddress);
    boolean loopbackAddress = inetAddr.isLoopbackAddress();
    log(133, "addr isLoopbackAddress " + loopbackAddress);
    boolean multicastAddress = inetAddr.isMulticastAddress();
    log(133, "addr isMulticastAddress " + multicastAddress);
    boolean siteLocalAddress = inetAddr.isSiteLocalAddress();
    log(133, "addr isSiteLocalAddress " + siteLocalAddress);

    return (!anyLocalAddress) && (!linkLocalAddress) && (!loopbackAddress) && (!multicastAddress) && (!siteLocalAddress);
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

  public static NetworkInterface getHostInterface() {
    return hostInterface;
  }

  public static void setHostInterface(NetworkInterface a2) {
    hostInterface = a2;
  }
}