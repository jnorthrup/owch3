package owch;

/**
 * ServerWrapper
 *
 * use this to write Socket Class Wrappers for udp, and tcp, so we can
 * kill a socket and throw an exception in all its waiting threads
 */
public interface ServerWrapper
{
    public int getLocalPort();
    public void close();
};

