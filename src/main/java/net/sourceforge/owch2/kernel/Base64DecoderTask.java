package net.sourceforge.owch2.kernel;

import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

class Base64DecoderTask extends AbstractBlockCodecTask {

    final byte[] ASCII_BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
    final int _8_BIT = 0xff;
    final int _6_BIT = 0x3f;
    final byte EQUAL = '=';
    final byte[] EOL = "\r\n".getBytes();


    int[] IA = new int[256];
    private static final int TRIPLES_PER_LINE = 19;
    private static final int PROBESIZE = TRIPLES_PER_LINE * 4;

    public Base64DecoderTask(Exchanger<ByteBuffer> writeX, Exchanger<ByteBuffer> readX) {
        super(writeX, readX);
    }

    {
        Arrays.fill(IA, -1);
        for (
                int i = 0, iS = ASCII_BASE.length;
                i < iS; i++)
            IA[ASCII_BASE[i]] = i;
        IA['='] = 0;
    }


    /**
     * here we allow some sludge for recognizing the EOL as noise...
     *
     * @param rxBlock
     * @param wxBlock
     */
    protected void blockX(ByteBuffer rxBlock, ByteBuffer wxBlock) {

        {
            //noise-reduction here
            ByteBuffer dupe = rxBlock.duplicate();
            while (dupe.hasRemaining() && IA[dupe.get()] < 1) ;
            rxBlock.position(dupe.position() - 1);
        }

        while (rxBlock.remaining() > 4) {
            int i = IA[rxBlock.get()] << 18;
            i |= IA[rxBlock.get()] << 12;
            wxBlock.put((byte) (i >> 16));
            i |= IA[rxBlock.get()] << 6;
            wxBlock.put((byte) (i >> 8));
            i |= IA[rxBlock.get()];
            wxBlock.put((byte) i);
        }
    }

    protected void finalX(ByteBuffer rxBlock, ByteBuffer wxBuf) {
        int i = 0;
        i |= IA[rxBlock.get()] << 18;
        byte f1 = rxBlock.get();
        i |= IA[f1] << 12;
        // Add the bytes
        if (f1 == '=') return;
        wxBuf.put((byte) (i >> 16));
        f1 = rxBlock.get();
        i |= IA[f1] << 6;
        if (f1 == '=') return;
        wxBuf.put((byte) (i >> 8));
    }
}
