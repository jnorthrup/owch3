package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;

/**
 * $Id: PipeConnector.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 *
 * @author James Northrup
 * @version $Id: PipeConnector.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class PipeConnector extends TCPServerWrapper implements ListenerReference, Runnable {
    int threads;

    public ProtocolType getProtocol() {
        return ProtocolType.Pipe;
    }


    public long getExpiration() {
        return (long) 0;
    }

    public int getThreads() {
        return threads;
    }

    public ServerWrapper getServer() {
        return this;
    }


    public void expire() {
        getServer().close();
    }

    PipeConnector(InetAddress hostAddr, int port, int threads) throws IOException {
        super(port, hostAddr);
        this.threads = threads;
        try {
            for (int i = 0; i < threads; i++) {
                new Thread(this).start();
            }
        }
        catch (Exception e) {
        }
    }


    public void run() {
        while (!Env.getInstance().shutdown) {
            try {
                Socket s = accept();
            }
            catch (Exception e) {
            }
        }
    }

}
//$Log: PipeConnector.java,v $
//Revision 1.3  2005/06/03 18:27:47  grrrrr
//no message
//
//Revision 1.2  2005/06/01 06:43:11  grrrrr
//no message
//
//Revision 1.1.1.1  2002/12/08 16:05:51  grrrrr
//
//
//Revision 1.1.1.1  2002/12/08 16:41:53  jim
//
//
//Revision 1.3  2002/05/19 21:34:09  grrrrr
//intellij damage
//
//Revision 1.2  2002/05/17 07:54:08  grrrrr
//gratuitous together/J formatting.
//
//Revision 1.1.1.1  2002/05/11 18:57:11  grrrrr
//new Features:
//
//IRC agent
//idyuts has been incorporated and folded in
//some SWing gui work on the IRC agent, proof its possible
//new package names
//
//Revision 1.3  2001/09/23 10:20:10  grrrrr
//lessee
//
//2 major enhancements
//
//1) we now use reflection to decode message types.
//
//a message looks for handle_<JMSType> method that takes a MetaProperties as its input
//
//2) we now serve HTTP / 1.1 at every opportunity, sending
// content-length, and last-modified, and content type by default.
// (MobilePayload still needs a few updates to catch up)
//
//Revision 1.1.1.1.2.1  2001/04/30 04:27:56  grrrrr
//SocketProxy + Deploy app
//


