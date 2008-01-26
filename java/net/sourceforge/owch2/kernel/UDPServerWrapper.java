package net.sourceforge.owch2.kernel;

import java.net.*;

/*
*
* UDPServerWrapper
*
*/

/**
 * @author James Northrup
 * @version $Id$
 */
public class UDPServerWrapper extends DatagramSocket implements net.sourceforge.owch2.kernel.ServerWrapper {
    public UDPServerWrapper(InetAddress hostAddr, int port) throws SocketException {
        super(port, hostAddr);
    }
}

