package net.sourceforge.owch2.kernel;

import static net.sourceforge.owch2.kernel.Reactor.*;

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
    static final char[] colon = ": ".toCharArray();
    private int bufferSize = getInstance().getCacheBuffer().capacity();

    public Exchanger<ByteBuffer> send(final Map.Entry<CharSequence, Object>... event) throws InterruptedException {

        final Exchanger<ByteBuffer> sendX = new Exchanger<ByteBuffer>();
        final StringBuilder builder = new StringBuilder();
        getInstance().submit(new Callable<Exchanger<ByteBuffer>>() {
            ByteBuffer cacheBuf = getInstance().getCacheBuffer();
            CharBuffer buffer = cacheBuf.duplicate().asCharBuffer();

            public Exchanger<ByteBuffer> call() throws Exception {
                cacheBuf.rewind();

                for (Map.Entry<CharSequence, Object> entry : event) {
                    builder.append(/*URLEncoder.encode(*/entry.getKey().toString()/*, "UTF-8")*/).append(colon);
                    builder.append(/*URLEncoder.encode(*/String.valueOf(entry.getValue()))/*), "UTF-8")*/;
                    builder.append('\n');
//                    flip();
                    builder.delete(0, builder.length());
                }
                buffer.append('\n');
                sendX.exchange((ByteBuffer) cacheBuf.rewind());

                return sendX;
            }

            ;

//            private void flip() throws InterruptedException, InvalidPropertiesFormatException {
//                if (builder.length() <= buffer.length()) {
//                    if (builder.length() > buffer.remaining()) {
//                        cacheBuf = (ByteBuffer) sendX.exchange(cacheBuf).flip();
//                        buffer = cacheBuf.asCharBuffer();
//                        buffer.append(builder);
//                    }
//                    return;
//                }
//                throw new InvalidPropertiesFormatException("output line exceeds " + bufferSize);
//            }
        });
        return sendX;
    }


    /**
     * howto:
     * <p/>
     * you send me the Exchanger.  I send you the future for the spinning routine.
     * when you want the results you do future.get;
     * <p/>
     * you fill the exchanger and send a null pointer to get the event back.
     *
     * @param readX
     * @return
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws UnsupportedEncodingException
     */
    public Future<Iterable<Map.Entry<CharSequence, Object>>> recv(final Exchanger<ByteBuffer> readX) {

        //got the readX
        Callable<Iterable<Map.Entry<CharSequence, Object>>> callable = new Callable<Iterable<Map.Entry<CharSequence, Object>>>() {


            String line = "";
            ByteBuffer buffer;
            boolean completion = false;

            public Iterable<Map.Entry<CharSequence, Object>> call() throws InterruptedException {
                List<Map.Entry<CharSequence, Object>> event = null; //the future is here
                ListIterator<Map.Entry<CharSequence, Object>> event_put = null;


                try {
                    buffer = getInstance().getCacheBuffer();

                    CharBuffer charBuffer = buffer.asCharBuffer();
                    StringBuilder bounce = null;

                    while (buffer != null) {
                        buffer = readX.exchange(buffer);
                        StringBuilder key = null;
                        StringBuilder val = null;
                        boolean escaping = false;
                        boolean commenting = false;
                        int keysep = -1;
                        int indent = 0;
                        while (charBuffer.hasRemaining()) {
                            char c = charBuffer.get();
                            if (escaping) continue;
                            final int pos = charBuffer.position();
                            switch (c) {

                                case '\\':
                                    escaping = true;
                                    break;
                                case '#':
                                    commenting |= pos == indent + 1 && key == null;
                                    break;
                                case ':':
                                    if (!commenting) keysep = pos;
                                    break;
                                case ' ':
                                    if (key == null) {
                                        if (!commenting) {
                                            if (keysep != pos - 1) {
                                                if (pos == indent + 1) indent++;
                                            } else {
                                                if (event == null) {
                                                    event = new ArrayList<Map.Entry<CharSequence, Object>>();
                                                    event_put = event.listIterator();
                                                }

                                                key = new StringBuilder();
                                                if (bounce != null) {
                                                    key.append(bounce);
                                                    bounce = null;
                                                }
                                                key.append(key).append(charBuffer.duplicate().position(keysep - 1).flip().toString());
                                                charBuffer = charBuffer.slice();
                                            }
                                        }
                                    }
                                    break;
                                case '\n':
                                    if (!commenting) {
                                        if (key == null) {
                                            if (pos == 1) {
                                                completion = true;
                                            }
                                        } else {
                                            val = new StringBuilder();
                                            if (bounce != null) {
                                                val.append(bounce);
                                                bounce = null;
                                            }
                                            val.append(charBuffer.duplicate().position(pos - 1).flip().toString());
                                            final String k = URLDecoder.decode(key.toString(), "UTF-8").trim();
                                            final String v = URLDecoder.decode(val.toString(), "UTF-8").trim();

                                            event_put.add(new Map.Entry<CharSequence, Object>() {
                                                public CharSequence getKey() {
                                                    return k;  //To change body of implemented methods use File | Settings | File Templates.
                                                }

                                                public Object getValue() {
                                                    return v;  //To change body of implemented methods use File | Settings | File Templates.
                                                }

                                                public Object setValue(Object value) {
                                                    throw new UnsupportedOperationException();
                                                }
                                            });

                                        }

                                        //this will set us up for a  new happy key next line
                                        charBuffer = charBuffer.slice();
                                    } else {
                                        commenting = false;
                                    }
                                default:
                                    break;
                            }

                            if (completion) break;
                        }
                        if (completion) break;
                        bounce = new StringBuilder(charBuffer.flip().toString());
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                return event;
            }
        };
        return (Future<Iterable<Map.Entry<CharSequence, Object>>>) getInstance().submit(callable);
    }


    public Exchanger<ByteBuffer> send(Iterable<Map.Entry<CharSequence, Object>> transaction) throws InterruptedException {
        Collection<Map.Entry<CharSequence, Object>> arr = new ArrayList<Map.Entry<CharSequence, Object>>();
        for (Map.Entry<CharSequence, Object> charSequenceObjectEntry : transaction)
            arr.add(charSequenceObjectEntry);
        return send((Map.Entry<CharSequence, Object>[]) arr.toArray());
    }
}




