package owch;

import java.net.*;
import java.io.*;
import java.util.*;

public class PipeFactory implements ListenerFactory {
    public boolean readyState = false;
    boolean alive = true;

    public boolean ready() {
        return readyState;
    };

    Hashtable sent = new Hashtable();

    /** ctor */
    public PipeFactory() {
    }

    public final MetaProperties getLocation() {
        return Env.getProtocolCache().getLocation("pipe");
    };

    public ListenerReference create(int port, int threads) {
        Thread t = null;
        PipeConnector Pipes;
        try {
            Pipes = new PipeConnector((int)port, threads);
        } catch (Exception e) {
            Env.debug(2, "PipeConnector init failure port " + port);
            return null;
        };
        for (int i = 0; i < Pipes.getThreads(); i++) {
            t = new Thread(Pipes, "PipeListener Thread #" + i + " / port " + Pipes.getLocalPort());
            t.setDaemon(true);
            t.start();
        };
        return Pipes;
    };
};
