package net.sourceforge.owch2.kernel;

import java.io.*;
import java.nio.*;
import java.util.concurrent.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public interface Format {
    Future<Exchanger<ByteBuffer>> send(final EventDescriptor event) throws InterruptedException;

    EventDescriptor recv(Exchanger<ByteBuffer> fBufX) throws InterruptedException, ExecutionException, UnsupportedEncodingException;
}



