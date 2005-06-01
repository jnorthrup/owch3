package net.sourceforge.owch2.kernel;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/*
*
* UDPServerWrapper
*
*/

/**
 * @author James Northrup
 * @version $Id: UDPServerWrapper.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class UDPServerWrapper extends DatagramSocket implements net.sourceforge.owch2.kernel.ServerWrapper {
    public UDPServerWrapper(InetAddress hostAddr, int port) throws SocketException {
        super(port, hostAddr);
    }

    ;
}

;


