package net.sourceforge.owch2.kernel;

/**
*
* ListenerReference
*
*
 * @version $Id: ListenerReference.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public interface ListenerReference {
    public void expire();

    public  ServerWrapper getServer();

    public String getProtocol(); //used in the process of creating the URL

    public int getThreads(); //0==Parent Server Default

    public long getExpiration(); //Client sets this before adding to a Queue
    //this defines a context switch wherein
}

;


