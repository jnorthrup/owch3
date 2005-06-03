package net.sourceforge.owch2.kernel;

import java.net.*;

/*
*
* UDPServerWrapper
*
*/

/**
 * @author James Northrup
 * @version $Id: UDPServerWrapper.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class UDPServerWrapper extends DatagramSocket implements net.sourceforge.owch2.kernel.ServerWrapper {
    public UDPServerWrapper(InetAddress hostAddr, int port) throws SocketException {
        super(port, hostAddr);
    }
}

