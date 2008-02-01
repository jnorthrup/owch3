package net.sourceforge.owch2.kernel;

import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public interface Format {
    Exchanger<ByteBuffer> send(final Map.Entry<CharSequence, Object>... event) throws InterruptedException;

    Future<Iterable<Map.Entry<CharSequence, Object>>> recv(Exchanger<ByteBuffer> fBufX);
}
