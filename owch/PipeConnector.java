package owch;

import java.net.*;
import java.util.*;
import java.io.*;

/**
$Id: PipeConnector.java,v 1.1.1.1.2.1 2001/04/30 04:27:56 grrrrr Exp $
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
        while (true) {
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
//Revision 1.1.1.1.2.1  2001/04/30 04:27:56  grrrrr
//SocketProxy + Deploy methods
//
