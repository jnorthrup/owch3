package owch;

import java.net.*;
import java.util.*;
import java.io.*;

/**
$Id: PipeConnector.java,v 1.3 2001/09/23 10:20:10 grrrrr Exp $
 * @version $Id: PipeConnector.java,v 1.3 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup
 */
public class PipeConnector extends TCPServerWrapper implements ListenerReference, Runnable {
    int threads;

    public String getProtocol() {
        return "pipe";
    };

    public long getExpiration() {
        return (long)0;
    }

    public int getThreads() {
        return threads;
    }

    public ServerWrapper getServer() {
        return this;
    };

    public void expire() {
        getServer().close();
    };

    PipeConnector(int port, int threads) throws IOException {
        super(port);
        this.threads = threads;
        try {
	    for (int i=0;i<threads;i++)
		new Thread(this).start();
        }
        catch (Exception e) {
            Env.debug(2, "ServerSocket creation Failure:"+e.getMessage());
        };
    };

    public void run() {
        while (!Env.shutdown) {
            try {
                Socket s = accept();
                Env.debug(20, "debug: " + Thread.currentThread().getName() + " init");
            } catch (Exception e) {
                Env.debug(2, "PipeServer thread going down in flames");
            };
        };
    };
};


//$Log: PipeConnector.java,v $
//Revision 1.3  2001/09/23 10:20:10  grrrrr
//lessee
//
//2 major enhancements
//
//1) we now use reflection to decode message types.
//
//a message looks for handle_<JMSType> method that takes a MetaProperties as its input
//
//2) we now serve HTTP / 1.1 at every opportunity, sending content-length, and last-modified, and content type by default.  (WebPage still needs a few updates to catch up)
//
//Revision 1.1.1.1.2.1  2001/04/30 04:27:56  grrrrr
//SocketProxy + Deploy methods
//
