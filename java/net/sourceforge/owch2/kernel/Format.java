package net.sourceforge.owch2.kernel;

import java.nio.*;
import java.util.concurrent.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public interface Format {
    Exchanger<ByteBuffer> send(final EventDescriptor event) throws InterruptedException;

    Future<EventDescriptor> recv(Exchanger<ByteBuffer> fBufX);
}
