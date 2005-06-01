package net.sourceforge.owch2.kernel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/*
*
* TCPServerWrapper
*
*/

/**
 * @author James Northrup
 * @version $Id: TCPServerWrapper.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class TCPServerWrapper implements ServerWrapper {
    ServerSocket s;

    //TODO: figure out a cleaner way to do
            public TCPServerWrapper(int port, InetAddress hostAddr) throws IOException {
        s = new ServerSocket(port, 16, hostAddr);
    }

    ;

    public final void close() {
        try {
            s.close();
        }
        catch (Exception e) {
        }
        ;
    }

    ;

    public final Socket accept() throws IOException {
        return s.accept();
    }

    ;

    public final int getLocalPort() {
        return s.getLocalPort();
    }

    ;

    public final ServerSocket serverSocket() {
        return s;
    }

    ;
}

;


