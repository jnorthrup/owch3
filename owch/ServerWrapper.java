package owch;

/**
 * ServerWrapper
 *
 * use this to write Socket Class Wrappers for udp, and tcp, so we can
 * kill a socket and throw an exception in all its waiting threads
 * @version $Id: ServerWrapper.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup
 */
public interface ServerWrapper
{
    public int getLocalPort();
    public void close();
};

