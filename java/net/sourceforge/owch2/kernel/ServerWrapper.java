package net.sourceforge.owch2.kernel;

/**
 * ServerWrapper use this to write Socket Class Wrappers for udp, and
 * tcp, so we can kill a socket and throw an exception in all its waiting threads
 *
 * @author James Northrup
 * @version $Id: ServerWrapper.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public interface ServerWrapper {
    public int getLocalPort();

    public void close();
}


