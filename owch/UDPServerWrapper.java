package owch;

import java.net.*;
import java.io.*;

/*
 *
 * UDPServerWrapper
 *
 */

/**
 * @version $Id: UDPServerWrapper.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
public class UDPServerWrapper extends DatagramSocket implements ServerWrapper
{
  
    public UDPServerWrapper(int port)	throws SocketException
    {
	super(port);
    };
};
