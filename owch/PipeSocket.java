package owch;

import java.net.*;
import java.util.*;
import java.io.*;

/** GateKeeper opens a PipeSocket to send data ussually in one direction. */
public class PipeSocket {
    //for GET method the inner socket OutputStream will close
    //first.
    boolean isGet = false;
    //for PUT method the outer socket InputStream will close
    //first.
    boolean isPut = false;
    Socket uc, oc;
    InputStream oi;
    InputStream ci;
    OutputStream oo;
    OutputStream co;
    ThreadGroup tg;
    static int sc = 0;
    String label = "Generic Pipe" + sc++;

    /**
     * pass in the requesting web socket, a MetaNode with an URL defining the serving host/port, and a request stolen from a
     * GateKeeper incoming connection.  This literally opens up a proxy connection to said service and passes the information
     * along. TODO: look up simple web proxy semantics for connection headers.
     */
    public PipeSocket(Socket o) {
        oc = o;
    }

    public void connectTarget(Socket s) {
        try {
            //TODO: support file uploads.
            uc = s;
            oc.setReceiveBufferSize(32 * 1024);
            uc.setReceiveBufferSize(32 * 1024);
            oc.setSendBufferSize(32 * 1024);
            uc.setSendBufferSize(32 * 1024);
            oi = new BufferedInputStream(oc.getInputStream());
            ci = new BufferedInputStream(uc.getInputStream());
            oo = new BufferedOutputStream(oc.getOutputStream());
            co = new BufferedOutputStream(uc.getOutputStream());
            uc.setSoTimeout(1000 * 15 * 60 * 60); //15 minutes?
            oc.setSoTimeout(1000 * 15 * 60 * 60); //15 minutes?
        }
        catch (Exception e) {
            e.printStackTrace();
        };
    };

    public void spin() {
        tg = new ThreadGroup("TG:" + label);
        new PipeThread(uc, oi, co, true, "PTInput:" + label); //
        new PipeThread(oc, ci, oo, true, "PTOutput:" + label); //
    };

    /**
     *   worker thread for the PipeSocket.  symetrical.java has a bug which restricts socket closes to be all or
     * nothing. therefore we cannot close "half" of our pipe without killing the other half.  This means that "terminate"
     * booleans are to indicate whether the stream wille cose the pipe when it hits EOF or will wait for the other side
     * indefinitely. Sun says "NOT A BUG".  its a good idea therefore to use a C web server or proxy.  :-(
     */
    public class PipeThread implements Runnable {
        InputStream is;
        OutputStream os;
        boolean term;
        int actual, avail;
        Object pipe;
        final int blocksize = 18 * 1024;
        byte[] buf = new byte[blocksize];

        public PipeThread(Object closeable, InputStream i, OutputStream o, boolean terminate, String name) {
            pipe = closeable;
            is = i;
            os = o;
            term = terminate;
        
            new Thread(tg, this, name).start();
        };

        /** worker thread */
        public void run() {
            try {
                while (true) { //spin around indefinitely
                    for (avail = is.available(); avail > 0; ) { //runs while
                        //data exists
                        //to be
                        //claimed
                        Env.debug(500, label + " read has available bytes: " + avail);
                        actual = is.read(buf);
                        if (actual == -1) {
                            os.flush();
                            Env.debug(15, label + " input stream closed " + actual);
                            if (term) {
                                os.close();
                                //close something...
                                pipe.getClass().getMethod("close",
                                    new Class[] { }).invoke(pipe,
                                    new Object[] { });
                                //interrupt our sister thread... which
                                //should be asleep
                                tg.interrupt();
                            };
                            return;
                        };
                        Env.debug(500, label + " output: " + actual);
                        os.write(buf, 0, actual);
                    };
                    //we avoid blocking in case we need to be
                    //interrupted by our sister thread.
                    Thread.currentThread().sleep(100);
                };
            } catch (Exception e) {
                Env.debug(500, label + " closing: " + e.getMessage());
            }
        };
    };
};
