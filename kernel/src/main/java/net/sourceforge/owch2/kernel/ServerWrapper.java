package net.sourceforge.owch2.kernel;

/**
 * ServerWrapper use this to write Socket Class Wrappers for udp, and
 * tcp, so we can kill a socket and throw an exception in all its waiting threads
 *
 * @author James Northrup
 * @version $Id$
 */
public interface ServerWrapper {
    public int getLocalPort();

    public void close();
}


