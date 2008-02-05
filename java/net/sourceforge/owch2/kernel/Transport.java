package net.sourceforge.owch2.kernel;

import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * User: jim
 * Date: Jan 29, 2008
 * Time: 11:06:55 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Transport {
    Map<CharSequence, Agent> getLocalAgents();

    URI getURI() throws SocketException, URISyntaxException;

    Short getPort();

    boolean hasPath(CharSequence name);

    void setHostAddress(InetAddress hostAddress);

    void setHostInterface(NetworkInterface hostInterface);

    void setPort(Short port);

    void setSockets(Integer sockets);

    void setThreads(Integer threads);


    Format getFormat();

    void recv(HasDestination notification);

    Future<Exchanger<ByteBuffer>> send(HasDestination notification);
}

