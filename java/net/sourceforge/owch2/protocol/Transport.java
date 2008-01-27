package net.sourceforge.owch2.protocol;

import net.sourceforge.owch2.kernel.*;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
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
    Default,
    Null,
    Pipe,
    UdpChannel;

    ExecutorService threadPool;

    Router router;
    InetAddress hostAddress;
    NetworkInterface hostInterface;
    Short port;
    Integer sockets;
    Integer threads;
    private Location location;

    Transport() {
        this.init();
    }

    public void init() {
        String routerClassName = null;
        try {
            routerClassName = getClass().getPackage().getName() + ".router." + name() + "Router";
            Class<?> routerClass = Class.forName(routerClassName);
            this.router = (Router) routerClass.newInstance();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Error("Router mismatch finding " + routerClassName);
        }

    }


    public Router getRouter() {
        return router;
    }

    public Location getLocation() {
        return location;
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

    public Short getPort() {
        return port;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Reference<Map> getPathMap() {
        return router.getPathMap();  //Todo: verify for a purpose
    }

    public Location getPath(MetaAgent destination) {
        return router.getPath(destination);//Todo: verify for a purpose
    }

    public void send(Message... async) {
        router.send(async);
    }

    public Serializable sendWithLog(Message... logged) {
        return router.sendWithLog(logged);

    }

    public Reciept sendWithReceipt(Message... synMessages) {
        return router.sendWithReceipt(synMessages);  //Todo: verify for a purpose
    }

    public Reference<Observable> sendWithNotification(Message... syncMessages) {
        return router.sendWithNotification(syncMessages);
    }

    public boolean hasPath(String jmsdestination) {
        return router.hasPath();
    }
}
