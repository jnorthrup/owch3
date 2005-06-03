package net.sourceforge.owch2.kernel;

/**
 * ListenerReference
 *
 * @author James Northrup
 * @version $Id: ListenerReference.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public interface ListenerReference {
    public void expire();

    public ServerWrapper getServer();

    public ProtocolType getProtocol(); //used in the process of creating the URL

    public int getThreads(); //0==Parent Server Default

    public long getExpiration(); //Client sets this before adding to a Queue

}

