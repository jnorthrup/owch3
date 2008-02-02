package net.sourceforge.owch2.kernel;

import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

public class Base64EncoderTask extends AbstractBlockCodecTask {

    final byte[] ASCII_BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
    final int _8_BIT = 0xff;
    final int _6_BIT = 0x3f;
    final byte EQUAL = '=';
    final byte[] EOL = "\r\n".getBytes();
    private final int TRIPLES_PER_LINE = 19;

    public Base64EncoderTask(Exchanger<ByteBuffer> readX, Exchanger<ByteBuffer> writeX, Map.Entry<CharSequence, Object>... properties) {
        super(writeX, readX);
    }


    protected void blockX(ByteBuffer rxBlock, ByteBuffer wxBlock) {
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
        wxBlock.put(EOL);
    }

    protected void finalX(ByteBuffer rxBlock, ByteBuffer wxBuf) {
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


}
