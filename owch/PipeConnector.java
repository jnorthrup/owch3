package owch;

import java.net.*;
import java.util.*;
import java.io.*;

/**
 * Advertise a pipe connnector to the GateKeeper when you have a
 * request waiting.  Gatekeeper will store 1:1 registration:pipeline.
 * thus if you register 25 seperate hierarchies you get 25 seperate
 * pipelines waiting.  Pipelines can hold as many threads as needed
 * and serve as many instances as needed.  Each Web Object needs its
 * own seperate ListenerCache. BIG DIFFERENCE -- owch and httpd protocols use
 * pooled caches for all agents.
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
                new Thread(this).start();
        }
        catch (Exception e) {
            Env.debug(2, "ServerSocket creation Failure");
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
