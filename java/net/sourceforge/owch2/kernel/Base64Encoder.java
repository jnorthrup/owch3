package net.sourceforge.owch2.kernel;

import java.io.*;
import static java.lang.Math.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;


class Base64Encoder implements Codec {
    private int writeBlockSize;
    private int readBlockSize;
    final byte[] ASCII_BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
    final int _8_BIT = 0xff;
    final int _6_BIT = 0x3f;
    final byte EQUAL = '=';
    final byte[] EOL = "\r\n".getBytes();


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
    public final Future<Exchanger<ByteBuffer>> encode(final Exchanger<ByteBuffer> readX, final Exchanger<ByteBuffer> writeX,
                                                      Map.Entry... Properties) throws IOException, InterruptedException {
        // must be rxBuf.limit() % 3 == 0
        return Reactor.getThreadPool().submit(
                new Callable<Exchanger<ByteBuffer>>() {
                    public Exchanger<ByteBuffer> call() throws Exception {

                        final int TRIPLES_PER_LINE = 19;
                        readBlockSize = TRIPLES_PER_LINE * 3;
                        writeBlockSize = readBlockSize + 2;


                        ByteBuffer rxBuf = (ByteBuffer) Reactor.getCacheBuffer().clear();
                        ByteBuffer tailBuf = (ByteBuffer) Reactor.getCacheBuffer().clear();
                        ByteBuffer wxBuf = (ByteBuffer) Reactor.getCacheBuffer().clear();

                        do {

                            try {
                                ByteBuffer swap = rxBuf;
                                rxBuf = null;
                                rxBuf = readX.exchange(swap);
                            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            boolean closed = null == rxBuf;


                            if (0 != tailBuf.position()) {
                                if (null != rxBuf) {
                                    if (rxBuf.remaining() < readBlockSize)
                                        tailBuf.limit(readBlockSize);
                                }

                                //rxBuf now changes position
                                //rxBuf
                                final ByteBuffer filler;
                                if (null != rxBuf) {
                                    filler = (ByteBuffer) ((ByteBuffer)
                                            rxBuf.position(min(rxBuf.remaining(), tailBuf.remaining()))).duplicate().flip();
                                    tailBuf.put(filler);
                                }
                                //aprrently we're getting only a trickle....
                                if (tailBuf.hasRemaining() && !closed)
                                    continue;

                                encodeBuffer(tailBuf, wxBuf, writeX, closed);
                                tailBuf.clear();

                            }
                            if (!closed) encodeBuffer(rxBuf, wxBuf, writeX, false);
                            int rem = 0x0;
                            if (null != rxBuf) {
                                rem = rxBuf.remaining();
                            }
                            final int fringe = rem % readBlockSize;
                            int newlim = rem - fringe;
                            tailBuf.clear();
                            tailBuf = tailBuf.put(((ByteBuffer) rxBuf.position(rxBuf.position() + newlim)).slice());
                            rxBuf.flip();
                            wxBuf = writeX.exchange((ByteBuffer) wxBuf.flip());
                            rxBuf = readX.exchange(rxBuf);
                        } while (null != rxBuf);
                        return writeX;
                    }


                    /**
                     * the work happens in here.
                     *
                     * @param rxBuf
                     * @param wxBuf
                     * @param writeX
                     * @param close
                     * @throws InterruptedException
                     */
                    private void encodeBuffer(ByteBuffer rxBuf, ByteBuffer wxBuf, Exchanger<ByteBuffer> writeX, boolean close) throws InterruptedException {

                        while (rxBuf.hasRemaining()) {
                            final int wxRemainingBlocks = wxBuf.remaining() / writeBlockSize;
                            if (wxRemainingBlocks < 1) {
                                ByteBuffer swap = wxBuf;
                                wxBuf = null;
                                wxBuf = writeX.exchange(swap);
                                continue;
                            }
                            close |= wxBuf == null;
//            int pos = wxBuf.position();
                            ByteBuffer wxBlock = ByteBuffer.allocate(writeBlockSize);
                            final byte[] bounceBbuf = new byte[4];
//            final ByteBuffer bbuf = ByteBuffer.wrap(bounceBbuf);
//            bbuf.order(ByteOrder.LITTLE_ENDIAN);


                            while (rxBuf.hasRemaining() && ((rxBuf.remaining() / readBlockSize > 0) || close)) {
                                final int start = rxBuf.position();
                                final int skip = min(readBlockSize, rxBuf.remaining());
                                final ByteBuffer buffer = (ByteBuffer) rxBuf.position(start + skip);

                                final ByteBuffer rxBlock = (ByteBuffer) buffer.duplicate().limit(start + skip);
                                encodeBlock(rxBlock, wxBlock);
                                encodeFinal(rxBlock, wxBuf);
                                wxBlock.put(EOL);
                            }
                        }

                    }

                    private void encodeBlock(ByteBuffer rxBlock, ByteBuffer wxBlock) {
                        byte[] bounceBuf = new byte[4];
                        while (rxBlock.remaining() > 2) {
                            // Copy next three bytes into lower 24 bits of int, paying
                            // attension to sign.
                            int i = (rxBlock.get() & _8_BIT) << 16 | (rxBlock.get() & _8_BIT) << 8 | rxBlock.get() & _8_BIT;
                            // Encode the int into four chars
                            bounceBuf[3] = ASCII_BASE[i & _6_BIT];
                            bounceBuf[2] = ASCII_BASE[(i >>>= 6) & _6_BIT];
                            bounceBuf[1] = ASCII_BASE[(i >>>= 6) & _6_BIT];
                            bounceBuf[0] = ASCII_BASE[(i >>>= 6) & _6_BIT];

                            wxBlock.put(bounceBuf);
                        }
                    }

                    private void encodeFinal(ByteBuffer rxBlock, ByteBuffer wxBuf) {
                        byte[] bounceBuf = new byte[4];
                        //this handles the tail.
                        if (rxBlock.hasRemaining()) {

                            // Prepare the int
                            boolean second;
                            int i = (rxBlock.get() & _8_BIT) << 10 | ((second = rxBlock.hasRemaining()) ? (rxBlock.get() & _8_BIT) << 2 : 0);

                            // Set last four chars
                            bounceBuf[3] = EQUAL;
                            bounceBuf[2] = second ? ASCII_BASE[i & _6_BIT] : EQUAL;
                            bounceBuf[1] = ASCII_BASE[(i >>>= 6) & _6_BIT];
                            bounceBuf[0] = ASCII_BASE[i >> 6];
                            wxBuf.put(bounceBuf);
                        }
                    }

                });
    }
}
