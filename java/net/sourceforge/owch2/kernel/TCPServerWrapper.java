package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;

/*
*
* TCPServerWrapper
*
*/

/**
 * @version $Id: TCPServerWrapper.java,v 1.1 2002/12/08 16:05:51 grrrrr Exp $
 * @author James Northrup
 */
public class TCPServerWrapper implements ServerWrapper {
    ServerSocket s;

    //TODO: figure out a cleaner way to do
    public TCPServerWrapper(int port, InetAddress hostAddr) throws IOException {
        s = new ServerSocket(port, 16,hostAddr);
    };

    public final void close() {
        try {
            s.close();
        }
        catch (Exception e) {
        }
        ;
    };

    public final Socket accept() throws IOException {
        return s.accept();
    };

    public final int getLocalPort() {
        return s.getLocalPort();
    };

    public final ServerSocket serverSocket() {
        return s;
    };
}

;


