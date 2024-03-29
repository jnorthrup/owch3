package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import static java.lang.Thread.sleep;

/**
 * GateKeeper opens a PipeSocket to route data ussually in one direction.
 *
 * @author James Northrup
 * @version $Id$
 */
public class PipeSocket {
    //for GET method the inner socket OutputStream will close
    //first.
    boolean isGet = false;
    //for PUT method the outer socket InputStream will close
    //first.
    boolean isPut = false;
    Socket uc;
    Socket oc;
    InputStream oi;
    InputStream ci;
    OutputStream co;
    OutputStream oo;
    ThreadGroup tg;
    static int sc = 0;
    String label = "Generic Pipe" + sc++;

    /**
     * pass in the requesting web socket, a Notification with an URL defining the serving host/port, and a request stolen from a
     * GateKeeper incoming connection.  This literally opens up a proxy connection to said service and passes the information
     * along.
     * TODO: look up simple web proxy semantics for connection headers.
     */
    public PipeSocket(Socket o) {
        oc = o;
    }


    StreamDesc cEnc = new StreamDesc();
    StreamDesc oEnc = new StreamDesc();

    /**
     * pass in the requesting web socket, and two encoding classes
     */
    public PipeSocket(Socket o, StreamDesc in, StreamDesc out) {
        oc = o;
        cEnc = in;
        oEnc = out;
    }

    public static Object[] prepareStream(Socket sock, StreamDesc streamDesc) throws SocketException, IOException {
        InputStream reader;
        OutputStream writer;
        sock.setSoTimeout(200);
        final InputStream istream = sock.getInputStream();
        final OutputStream ostream = sock.getOutputStream();

        reader = streamDesc.usingInflate ? new InflaterInputStream(istream) : istream;
        writer = streamDesc.usingDeflate ? new DeflaterOutputStream(ostream, new Deflater(Deflater.FILTERED)) : ostream;

        if (streamDesc.buffered) {
            int bb = streamDesc.bufbuf > 0 ? streamDesc.bufbuf : 32 << 10;
            sock.setReceiveBufferSize(bb);
            sock.setSendBufferSize(bb);
            reader = new BufferedInputStream(reader, bb);
            writer = new BufferedOutputStream(writer, bb);
        }
        final Object ret[] = {reader, writer};
        return ret;
    }

    public void spin() {
        tg = new ThreadGroup("TG:" + label);
        new PipeThread(uc, oi, co, false, "PTInput:" + label, this.cEnc); //
        new PipeThread(oc, ci, oo, false, "PTOutput:" + label, this.oEnc); //
    }

    /**
     * worker thread for the PipeSocket.  symetrical.java has a bug which restricts socket closes to be all or
     * nothing. therefore we cannot close "half" of our Pipe without killing the other half.  This means that "terminate"
     * booleans are to indicate whether the stream wille cose the Pipe when it hits EOF or will wait for the other side
     * indefinitely. Sun says "NOT A BUG".  its a good idea therefore to use a C web server or proxy.  :-(
     */
    public class PipeThread implements Runnable {
        InputStream is;
        OutputStream os;
        boolean term;
        int actual;
        int avail;
        Object pipe;
        final int blocksize = 18 << 10;
        byte[] buf = new byte[blocksize];
        String name;
        StreamDesc sdesc;

        public PipeThread(Object closeable, InputStream istream, OutputStream ostream, boolean terminate, String name, StreamDesc streamdesc) {
            pipe = closeable;
            is = istream;
            os = ostream;
            term = terminate;
            this.name = name;
            new Thread(tg, this, name).start();
            sdesc = streamdesc;
        }


        /**
         * worker thread
         */
        public void run() {
            if (is instanceof InflaterInputStream)//((InflaterInputStream)is).
                buf = new byte[40];
            while (!term) {
                try {
                    for (avail = is.available(); avail > 0;) {
                        //runs while
                        //data exists
                        //to be
                        //claimed
                        actual = is.read(buf);
                        if (actual == -1) {
                            os.flush();
                            term = true;
                            if (term) {
                                os.close();
                                //close something...
                                pipe.getClass().getMethod("close",
                                        new Class[]{
                                        }).invoke(pipe,
                                        new Object[]{
                                        });
                                //interrupt our sister thread... which
                                //should be asleep
                                tg.interrupt();
                            }

                            return;
                        }
                        os.write(buf, 0, actual);
                    }
                    //we avoid blocking in case we need to be
                    //interrupted by our sister thread.

                    sleep(100);
                    if (os instanceof DeflaterOutputStream) {

                        os.flush();
                    }
                }
                catch (InterruptedIOException e) {
                    try {
                        os.flush();
                    }
                    catch (IOException e1) {
                    }
                }
                catch (InterruptedException e) {
                    return;
                }
                catch (Exception e) {
                    return;
                }
            }
        }
    }


    public void connectTarget(Socket s) throws IOException {
        Object[] i;
        i = prepareStream(oc, this.oEnc);
        this.oi = (InputStream) i[0];
        this.oo = (OutputStream) i[1];
        i = prepareStream(uc = s, this.cEnc);
        this.ci = (InputStream) i[0];
        this.co = (OutputStream) i[1];
    }
}




