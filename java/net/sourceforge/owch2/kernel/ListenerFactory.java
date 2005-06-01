package net.sourceforge.owch2.kernel;


/*
*
* ListenerFactory
*
*/

/**
 * @author James Northrup
 * @version $Id: ListenerFactory.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
abstract public class ListenerFactory {
    //  private boolean ready = false;
            //  private boolean alive = true;
            abstract ListenerReference create(java.net.InetAddress hostAddr, int port, int threads /*0==Parent Server Default*/);

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
}

;


