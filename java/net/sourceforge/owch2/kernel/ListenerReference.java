package net.sourceforge.owch2.kernel;

import net.sourceforge.owch2.protocol.*;

/**
 * ListenerReference
 *
 * @author James Northrup
 * @version $Id$
 */
public interface ListenerReference {
    public void expire();

    public ServerWrapper getServer();

    public Transport getProtocol(); //used in the process of creating the URL

    public int getThreads(); //0==Parent Server Default

    public long getExpiration(); //Client sets this before adding to a Queue

}

