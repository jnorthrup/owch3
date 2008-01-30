package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public class RFC822Format implements Format {
    private static final char[] colon = ": ".toCharArray();

    public RFC822Format() {
    }


    public Future<Exchanger<ByteBuffer>> send(final EventDescriptor event) throws InterruptedException {
        final Exchanger<ByteBuffer> exchanger = new Exchanger<ByteBuffer>();
        final StringBuilder builder = new StringBuilder();


        Callable<Exchanger<ByteBuffer>> byteBufferCallable = new Callable<Exchanger<ByteBuffer>>() {
            ByteBuffer cacheBuf = Reactor.getCacheBuffer();
            CharBuffer buffer = cacheBuf.duplicate().asCharBuffer();

            public Exchanger<ByteBuffer> call() throws Exception {
                cacheBuf.rewind();

                for (Map.Entry<String, ?> entry : event.entrySet()) {

                    builder.append(URLEncoder.encode(entry.getKey(), "UTF-8")).append(colon);
                    builder.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
                    builder.append('\n');

                    flip();
                    builder.delete(0, builder.length());
                }
                buffer.append('\n');

                flip();
                return exchanger;
            }

            private void flip() throws InterruptedException {
                if (builder.length() > buffer.length()) {
                    exchanger.exchange((ByteBuffer) ByteBuffer.wrap(builder.toString().getBytes()).rewind());
                } else if (builder.length() > buffer.remaining()) {
                    cacheBuf = (ByteBuffer) exchanger.exchange(cacheBuf).flip();
                    buffer = cacheBuf.asCharBuffer();
                    buffer.append(builder);
                }
            }
        };
        return Reactor.submit(byteBufferCallable);
    }


    public EventDescriptor recv(Exchanger<ByteBuffer> fBufX) throws InterruptedException, ExecutionException, UnsupportedEncodingException {
//        Exchanger<ByteBuffer> bufX = new Exchanger<ByteBuffer>();
        EventDescriptor event = new EventDescriptor();
        String line = "";
        ByteBuffer buffer = fBufX.exchange(Reactor.getCacheBuffer());

        do {
            CharBuffer charBuffer = buffer.asCharBuffer();
            charBuffer.mark();
            if (charBuffer.hasRemaining()) {
                do {
                    try {

                        if (charBuffer.get() == '\n') {
                            // we've found an Map.entry
                            // we know the value ends here.


                            int pos = charBuffer.position();
                            CharBuffer buf1 = (CharBuffer) charBuffer.duplicate().reset().limit(pos - 1);
                            buf1 = buf1.slice();
                            line += buf1.toString().trim();

                            do {
                                while (buf1.get() != ':') {
                                }
                            } while (buf1.get() != ' ');

                            //we should be on the start of the 'value'

                            String val = URLDecoder.decode(buf1.slice().limit(buf1.remaining() - 1).toString(), "UTF-8");

                            //we marked the beiginning of the key...
                            String key = URLDecoder.decode(buf1.position(buf1.position() - 2).flip().toString(), "UTF-8");
                            event.put(key, val);
                            line = "";
                        }
                    } catch (BufferUnderflowException e) {

                        line = ((CharBuffer) charBuffer.reset()).slice().toString();
                        buffer = (ByteBuffer) fBufX.exchange(buffer).rewind();

                    }
                } while (charBuffer.hasRemaining());
            }
        } while (!line.isEmpty());
        return event;
    }
}