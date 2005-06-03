package net.sourceforge.owch2.kernel;

import java.net.*;


/*
*
* ListenerFactory
*
*/

/**
 * @author James Northrup
 * @version $Id: ListenerFactory.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
abstract public class ListenerFactory {
    protected InetAddress hostAddress;
    protected int port;
    protected int threads;
    protected int socketCount;

    //  private boolean ready = false;
    //  private boolean alive = true;
    abstract ListenerReference create();

    public abstract net.sourceforge.owch2.kernel.MetaProperties getLocation();
    //    public boolean isReady() { return ready; }
    //    public void setReady( boolean ready ) { this.ready = ready; }
    // private Map sent      = new HashMap();
    // public Map getSent() { return sent; }
    // public void setSent( Map sent ) { this.sent = sent; }
    //  public boolean isAlive() { return alive; }
    // public void setAlive( boolean alive ) { this.alive = alive; }
    //params
    //port - requested port number 0=random
    //threads - the number of threads to monitor the listener.  0=default

    public int getThreads() {
        return threads;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    public int getPort() {
        return port;
    }

    public void setHostAddress(InetAddress hostAddress) {
        this.hostAddress = hostAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public void setSocketCount(int socketCount) {
        //To change body of created methods use File | Settings | File Templates.
        this.socketCount = socketCount;
    }
}