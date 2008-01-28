package net.sourceforge.owch2.kernel;

import net.sourceforge.owch2.protocol.*;

import java.io.*;
import java.nio.channels.*;
import static java.nio.channels.SelectionKey.*;
import java.util.concurrent.*;

/**
 * This converts (injects) network data into a factory that produces inbound Messages.
 *
 * @author James Northrup
 * @version $Id$
 */


public enum Reactor {

    /**
     * Accept Reactor are transport specific operations
     */
    ACCEPT(OP_ACCEPT) {

        boolean call(final SelectableChannel channel, final Transport transport) throws IOException {
            ServerSocketChannel schannel = (ServerSocketChannel) channel;
            SocketChannel socketChannel = schannel.accept();
            channel.configureBlocking(false);
            return transport.channelAccept(socketChannel);
        }
    },
    CONNECT(OP_CONNECT) {
        boolean call(final SelectableChannel channel, final Transport transport) throws IOException {
            SocketChannel socketChannel = (SocketChannel) channel;
            return socketChannel.finishConnect() && transport.channelConnect(channel);
        }},
    READ(OP_READ) {
        boolean call(final SelectableChannel channel, final Transport transport) throws IOException {
            return transport.channelRead((ByteChannel) channel);
        }},
    WRITE(OP_WRITE) {
        boolean call(SelectableChannel channel, Transport transport) throws IOException {
            return transport.channelWrite((ByteChannel) channel);
        }},;
    static Selector selector;


    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        staticInit();
    }

    private static void staticInit() {

        threadPool.execute(new Runnable() {
            public void run() {
                int i = 0;
                Env instance = Env.getInstance();
                while (!instance.shutdown)
                    try {
                        i = selector.select();
                        if (i >= 1)
                            for (final SelectionKey selectionKey : selector.selectedKeys())
                                for (final Reactor reactor : values())
                                    if (0 != (reactor.op & selectionKey.readyOps()))
                                        threadPool.submit(
                                                new Callable() {
                                                    public Boolean call() throws Exception {
                                                        boolean b = reactor.call(selectionKey.channel(), (Transport) selectionKey.attachment());
                                                        if (!b) selectionKey.cancel();
                                                        return b;
                                                    }
                                                });
                    } catch (IOException e) {
                        e.printStackTrace();  //ToDo: change body of catch statement use File | Settings | File Templates.
                    }


            }
        });
    }


    /**
     * react to the event.
     *
     * @return whether to re-use this object or remove it (and child if any) from the reactor
     */
    abstract boolean call(final SelectableChannel channel, final Transport transport) throws IOException;


    static ThreadPoolExecutor threadPool;


    private int op;

    Reactor(int op) {
        this.op = op;
    }

    void registerChannel(SelectableChannel channel, Transport transport) throws IOException {

        if (!channel.isOpen()) {
            return;
        }

        if (channel.isBlocking()) {
            channel.configureBlocking(false);
        }

        int valid = channel.validOps();
        channel.register(selector, valid, transport);
    }

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }
}


