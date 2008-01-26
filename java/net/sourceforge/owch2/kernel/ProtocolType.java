package net.sourceforge.owch2.kernel;

import net.sourceforge.owch2.router.*;

import java.net.*;
import java.util.*;

/**
 * Enumerates Protocols and Router Classes and some occasional Environemnt defaults.
 * <p/>
 * This class operates to hold a registry of protocols in the systems, and assigns routers and default port numbers as needed by design.
 *
 * @author James Northrup
 * @version $Id: ProtocolType.java,v 1.2 2005/06/04 02:26:24 grrrrr Exp $
 * @copyright All Rights Reserved Glamdring Inc.
 */

public enum ProtocolType {
    ipc(ipcRouter.class),
    owch(owchRouter.class, /*owchListener.class,*/ 2112),
    Http(httpRouter.class,/* httpServer.class,*/ 7070),
    Pipe(null,/* PipeConnector.class, */0),
    Domain(domainRouter.class),
    Default(DefaultRouter.class),
    Null(NullRouter.class);

    private Class<? extends Router> routerClass;
    //    private Class<? extends ListenerReference> listenerReference;
    private Number defaultPort = null;


    private Class<? extends Router> Router;
    private Integer threads;
    private InetAddress hostAddress;
    private NetworkInterface hostInterface;
    private Integer socketCount;
    private Integer hostThreads;


    public Router routerInstance() {
        Router routerInstance;
        routerInstance = routerMap.get(this);
        if (routerInstance == null) {
            try {
                routerInstance = routerClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            routerMap.put(this, routerInstance);
        }
        return routerInstance;
    }

    static private Map<ProtocolType, Router> routerMap = new EnumMap<ProtocolType, Router>(ProtocolType.class);


    static private Map<ProtocolType, ListenerCache> listenerCaches = new EnumMap<ProtocolType, ListenerCache>(ProtocolType.class);


    public ListenerCache ListenerCacheInstance() {
        ListenerCache listenerCacheInstance;
        listenerCacheInstance = listenerCaches.get(this);
        if (listenerCacheInstance == null) {
            listenerCacheInstance = new ListenerCache();
            listenerCaches.put(this, listenerCacheInstance);
        }
        return listenerCacheInstance;
    }

    static private Map<ProtocolType, ListenerFactory> listenerFactorys = new EnumMap<ProtocolType, ListenerFactory>(ProtocolType.class);
    private Class<? extends ListenerFactory> ListenerFactoryClass;


    public ListenerFactory ListenerFactoryInstance() {
        ListenerFactory lfInstance;
        lfInstance = listenerFactorys.get(this);

        if (lfInstance == null) {
            try {
                lfInstance = ListenerFactoryClass.newInstance();
                //new Object[]{hostAddress, defaultPort, getHostThreads()}
                lfInstance.setHostAddress(hostAddress);
                lfInstance.setPort(defaultPort.intValue());
                lfInstance.setThreads(hostThreads);
                lfInstance.setSocketCount(socketCount.intValue());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            listenerFactorys.put(this, lfInstance);
        }
        return lfInstance;
    }

    ProtocolType() {
    }

    ProtocolType(Class<? extends Router> routerClass) {
        this();
        this.routerClass = routerClass;
    }

    ProtocolType(Class<? extends Router> routerClass, /*Class<? extends ListenerReference>listenerReference, */Number defaultPort) {
        this(routerClass);
//        this.listenerReference = listenerReference;
        this.defaultPort = defaultPort;
    }

    public Class<? extends Router> getRouterClass() {
        return routerClass;
    }


    public Number getDefaultPort() {
        return defaultPort;
    }

    public static void shutdown() {
        if (Env.getInstance().shutdown) routerMap.clear();
    }

    //has no properties
    public Location getLocation() {
        ListenerCache lc = listenerCaches.get(this);
//        final InetAddress hostAddress = Env.getInstance().getHostAddress();
        if (lc == null) {
            {
                final ListenerFactory instance = listenerFactorys.get(this);
                ListenerReference l;
                l = instance.create();
                lc.put(l);
            }
        }
        ListenerCache l = (ListenerCache) lc;

        if (l != null) {
            return l.getLocation();
        }
        return null;
    }

    public void setListenerFactoryClass(Class<? extends ListenerFactory> listenerFactoryClass) {

        ListenerFactoryClass = listenerFactoryClass;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public void setDefaultPort(Number defaultPort) {
        this.defaultPort = defaultPort;
    }

    public void setHostAddress(InetAddress hostAddress) {
        this.hostAddress = hostAddress;
    }

    public void setHostInterface(NetworkInterface hostInterface) {
        this.hostInterface = hostInterface;
    }

    public void setSocketCount(Integer socketCount) {
        this.socketCount = socketCount;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    public Integer getHostThreads() {
        return hostThreads;
    }
}
