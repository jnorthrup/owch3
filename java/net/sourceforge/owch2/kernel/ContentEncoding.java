package net.sourceforge.owch2.kernel;

import java.nio.*;
import java.util.concurrent.*;


/**
 * Exchanges and transforms buffers .
 * two exchangers in succession.  exchanger named "first" gets a buffer, and performs the transform, and "second" exchanger delivers the output buffers.
 */
public interface ContentEncoding {


    Exchanger<ByteBuffer> encode(Exchanger<ByteBuffer> first) throws InterruptedException;

    Exchanger<ByteBuffer> decode(Exchanger<ByteBuffer> second);

}

