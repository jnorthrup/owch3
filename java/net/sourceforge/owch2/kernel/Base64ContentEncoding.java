package net.sourceforge.owch2.kernel;

import java.nio.*;
import java.util.concurrent.*;


class Base64ContentEncoding {
    public Exchanger<ByteBuffer> encode(Exchanger<ByteBuffer> readX) throws InterruptedException {


        ByteBuffer rxBuf = Reactor.getCacheBuffer();


        rxBuf.limit(rxBuf.limit() / 3 * 4);


        rxBuf = readX.exchange(rxBuf);


    }
}