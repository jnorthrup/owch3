package net.sourceforge.owch2.kernel;

import java.net.*;

/*
*
* UDPServerWrapper
*
*/

/**
 * @version $Id: UDPServerWrapper.java,v 1.1 2002/12/08 16:05:51 grrrrr Exp $
 * @author James Northrup
 */
public class UDPServerWrapper extends DatagramSocket implements net.sourceforge.owch2.kernel.ServerWrapper {
    public UDPServerWrapper(InetAddress hostAddr, int port) throws SocketException {
        super(port,hostAddr);
    };
}

;


