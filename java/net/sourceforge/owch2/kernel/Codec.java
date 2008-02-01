package net.sourceforge.owch2.kernel;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;


/**
 * Exchanges and transforms buffers .
 * two exchangers in succession.  exchanger named "first" gets a buffer, and performs the transform, and "second" exchanger delivers the output buffers.
 */
public interface Codec {

    /**
     * Encodes a raw InputStream into a BASE64 OutputStream representation readX
     * accordance with RFC 2045. This implementation was inspired by MIG Base64
     * {@link util.Base64#encodeToByte(byte[], boolean)}
     *
     * @param readX
     * @param writeX
     * @param Properties
     * @throws java.io.IOException
     */
    Future<Exchanger<ByteBuffer>> encode(Exchanger<ByteBuffer> readX, Exchanger<ByteBuffer> writeX,
                                         Map.Entry... Properties) throws IOException, InterruptedException;
}

