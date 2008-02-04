package net.sourceforge.owch2.kernel;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Exchanges and transforms buffers using a thread to block between 2 exchangers.
 * two exchangers in succession.  exchanger named "first" gets a buffer, and performs the transform, and "second" exchanger delivers the output buffers.
 */
public interface Codec {
    /**
     * bind two exchangers to perform a block-exchange operation in 1 thread
     *
     * @param readX      an ${@link java.util.concurrent.Exchanger<ByteBuffer>} which delivers bytebuffers that contain the source data
     * @param writeX     the exchanger that delivers post-transformed buffers
     * @param properties 0 or more key-value pairs defining parameters
     * @return A ${@link java.util.concurrent.Future} suitable for additional 'writeX' uses
     * @throws IOException
     * @throws InterruptedException
     */
    Future<Exchanger<ByteBuffer>> encode(Exchanger<ByteBuffer> readX, Exchanger<ByteBuffer> writeX,
                                         Map.Entry<CharSequence, Object>... properties) throws IOException, InterruptedException;
}