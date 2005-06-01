package net.sourceforge.owch2.kernel;

/**
 * ServerWrapper use this to write Socket Class Wrappers for udp, and
 * tcp, so we can kill a socket and throw an exception in all its waiting threads
 *
 * @author James Northrup
 * @version $Id: ServerWrapper.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public interface ServerWrapper {
    public int getLocalPort();


    public void close();
}

;


