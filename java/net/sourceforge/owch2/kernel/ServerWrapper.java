package net.sourceforge.owch2.kernel;

/**
 * ServerWrapper use this to write Socket Class Wrappers for udp, and
 * tcp, so we can kill a socket and throw an exception in all its waiting threads
 * @version $Id: ServerWrapper.java,v 1.1 2002/12/08 16:05:51 grrrrr Exp $
 * @author James Northrup
 */
public interface ServerWrapper {
    public int getLocalPort();


    public void close();
}

;


