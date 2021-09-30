package net.sourceforge.owch2.kernel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Future;


class Base64Encoder implements Codec {


    /**
     * Encodes a raw InputStream into a BASE64 OutputStream representation readX
     * accordance with RFC 2045. This implementation was inspired by MIG Base64
     * {@link util.Base64#encodeToByte(byte[], boolean)}
     *
     * @param readX
     * @param writeX
     * @param properties
     * @throws java.io.IOException
     */
    public final Future<Exchanger<ByteBuffer>> encode(final Exchanger<ByteBuffer> readX,
                                                      final Exchanger<ByteBuffer> writeX,
                                                      Map.Entry<CharSequence, Object>... properties)
            throws IOException, InterruptedException {
        return Reactor.getThreadPool().submit(
                new Base64EncoderTask(readX, writeX, properties));
    }

}

