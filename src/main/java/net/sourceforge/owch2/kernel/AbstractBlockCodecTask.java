package net.sourceforge.owch2.kernel;

import java.nio.*;
import java.util.concurrent.*;

/**
 * User: jim
 * Date: Feb 2, 2008
 * Time: 12:03:44 AM
 */

public abstract class AbstractBlockCodecTask implements Callable<Exchanger<ByteBuffer>> {
    private int writeBlockSize;
    private int readBlockSize;
    protected final Exchanger<ByteBuffer> readX;
    protected final Exchanger<ByteBuffer> writeX;

    public AbstractBlockCodecTask(Exchanger<ByteBuffer> writeX, Exchanger<ByteBuffer> readX) {
        this.writeX = writeX;
        this.readX = readX;
    }

    public Exchanger<ByteBuffer> call() throws Exception {

        setWriteBlockSize(readBlockSize + 2);


        ByteBuffer rxBuf = (ByteBuffer) Reactor.getCacheBuffer().clear();
        ByteBuffer tailBuf = (ByteBuffer) Reactor.getCacheBuffer().clear();
        ByteBuffer wxBuf = (ByteBuffer) Reactor.getCacheBuffer().clear();

        do {

            try {
                ByteBuffer swap = rxBuf;
                rxBuf = null;
                rxBuf = readX.exchange(swap);
            } catch (InterruptedException e) {
            }

            if (isBufferUnderflow(rxBuf, tailBuf, wxBuf)) continue;

            int rem = 0;
            if (!(null == rxBuf)) {
                encodeBuffer(rxBuf, wxBuf, writeX, false);
                rem = rxBuf.remaining();
            }
            final int fringe = rem % getReadBlockSize();
            int newlim = rem - fringe;
            tailBuf.clear();
            rxBuf.position(rxBuf.position() + newlim);
            tailBuf.put(rxBuf.slice());
            rxBuf.flip();
            wxBuf = writeX.exchange((ByteBuffer) wxBuf.flip());
            rxBuf = readX.exchange(rxBuf);
        } while (!(null == rxBuf));
        return writeX;
    }

    /**
     * buffer underflow occurs when we are not yet closed but we haven't filled one block.
     *
     * @param rxBuf
     * @param tailBuf
     * @param wxBuf
     * @return
     * @throws InterruptedException
     */
    private boolean isBufferUnderflow(ByteBuffer rxBuf, ByteBuffer tailBuf, ByteBuffer wxBuf) throws InterruptedException {

        final boolean finalBlock = null == rxBuf;
        if (0 != tailBuf.position()) {
            if (!finalBlock) {
                if (rxBuf.remaining() < getReadBlockSize())
                    tailBuf.limit(getReadBlockSize());
                final int gap = java.lang.Math.min(rxBuf.remaining(), tailBuf.remaining());
                tailBuf.put((ByteBuffer) ((ByteBuffer) rxBuf.position(gap)).duplicate().flip());
            }

            if (tailBuf.hasRemaining())
                if (!finalBlock)
                    return true;

            encodeBuffer(tailBuf, wxBuf, writeX, finalBlock);
            tailBuf.clear();
        }
        return false;
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
            final int wxRemainingBlocks = wxBuf.remaining() / getWriteBlockSize();
            if (wxRemainingBlocks < 1) {
                wxBuf = writeX.exchange(wxBuf);
                continue;
            }
            ByteBuffer wxBlock = ByteBuffer.allocate(getWriteBlockSize());

            while (rxBuf.hasRemaining() && ((rxBuf.remaining() / getReadBlockSize() > 0) || close)) {
                final int start = rxBuf.position();
                final int skip = java.lang.Math.min(getReadBlockSize(), rxBuf.remaining());
                final ByteBuffer buffer = (ByteBuffer) rxBuf.position(start + skip);
                final ByteBuffer rxBlock = (ByteBuffer) buffer.duplicate().limit(start + skip);
                blockX(rxBlock, wxBlock);
                finalX(rxBlock, wxBuf);
            }
        }
    }

    protected abstract void blockX(ByteBuffer rxBlock, ByteBuffer wxBlock);

    protected abstract void finalX(ByteBuffer rxBlock, ByteBuffer wxBuf);

    public int getWriteBlockSize() {
        return writeBlockSize;
    }

    public void setWriteBlockSize(int writeBlockSize) {
        this.writeBlockSize = writeBlockSize;
    }

    public int getReadBlockSize() {
        return readBlockSize;
    }

    public void setReadBlockSize(int readBlockSize) {
        this.readBlockSize = readBlockSize;
    }
}
