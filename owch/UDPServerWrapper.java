package owch;

import java.net.*;
import java.io.*;

/*
 *
 * UDPServerWrapper
 *
 */
public class UDPServerWrapper extends DatagramSocket implements ServerWrapper
{
  
    public UDPServerWrapper(int port)	throws SocketException
    {
	super(port);
    };
};
