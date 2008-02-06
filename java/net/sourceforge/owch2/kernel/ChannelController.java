package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;

/**
 * one source/sync of messages for multiple SelectableChannels.
 * <p/>
 * the exchanger mates with Format exchanger, also works as payload buffer exchanger.
 */
public interface ChannelController {
    URI getUri() throws SocketException, URISyntaxException;

    void init(Exchanger<ByteBuffer> swap);

    boolean channelAccept(SelectionKey key);

    boolean channelConnect(SelectionKey key);

    Exchanger<ByteBuffer> channelRead(SelectionKey key) throws ExecutionException, InterruptedException, IOException;

    Exchanger<ByteBuffer> channelWrite(SelectionKey key) throws InterruptedException, IOException;
}
