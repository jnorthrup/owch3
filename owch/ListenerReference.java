package owch;

/*
 *
 * ListenerReference
 *
 */

/**
 * @version $Id: ListenerReference.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
public interface ListenerReference
{
    public void expire();

    public ServerWrapper getServer();

    public String getProtocol(); //used in the process of creating the URL 

    public int getThreads(); //0==Parent Server Default

    public long getExpiration(); //Client sets this before adding to a Queue
    //this defines a context switch wherein
};

