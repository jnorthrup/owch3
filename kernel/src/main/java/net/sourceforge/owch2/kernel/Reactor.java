package net.sourceforge.owch2.kernel;

import net.sourceforge.owch2.protocol.ChannelController;

import java.io.IOException;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Timer;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static java.nio.channels.SelectionKey.*;

/**
 * Reactor design pattern singleton as presented by the letters E,N,U, and M.
 * <p/>
 * 1 unified thread pool
 * 1 unified selector
 * 1 unified timer
 * <p/>
 * all ChannelControllers are designed as ordered generic slotted tasks for state progression.
 * <p/>
 * Controllers can be built by overrriding the task return vlues with the reference to the desired next slot.
 * <p/>
 * e.g.  simple controller
 * <p/>
 * enum Agenda{ state1,state2,state3 };
 * enum ChannelController{ t1,t2 }
 * Reactor.READ(channe;ChannelController)
 *
 * @author James Northrup
 * @version $Id$
 */
public enum Reactor {
    /**
     * Accept Reactor are ChannelController specific operations
     */
    ACCEPT(OP_ACCEPT) {
        boolean call(SelectionKey key) throws IOException {
            return ((ChannelController) key.attachment()).channelAccept(key);
        }
    },
    CONNECT(OP_CONNECT) {
        boolean call(SelectionKey key) throws IOException {
            return ((ChannelController) key.attachment()).channelConnect(key);
        }},
    READ(OP_READ) {
        boolean call(SelectionKey key) throws IOException, ExecutionException, InterruptedException {
            return ((ChannelController) key.attachment()).channelRead(key);
        }},
    WRITE(OP_WRITE) {
        boolean call(SelectionKey key) throws IOException, InterruptedException {
            return ((ChannelController) key.attachment()).channelWrite(key);

        }},;

    static Selector selector;

    public static final int BUFFSIZE = 1024 << 4;
    static final int BUFFCOUNT = 128;
    private static ByteBuffer cache;
    private static int futureBufferCount = BUFFCOUNT;
    private static int currentBufferCount = 0;

    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        Init();
        cache = ByteBuffer.allocateDirect(BUFFCOUNT * BUFFSIZE);
    }

    private static void Init() {
        threadPool.execute(
                new Runnable() {
                    public void run() {
                        int i = 0;
                        Env instance = Env.getInstance();
                        while (!instance.shutdown)
                            try {

                                //this blocks
                                i = selector.select();
                                if (i >= 1) submit(
                                        (Callable) () -> {
                                            boolean b = false;
                                            for (final SelectionKey selectionKey : selector.selectedKeys())
                                                for (final Reactor reactor : values())
                                                    if (0 != (reactor.op & selectionKey.readyOps())) {
                                                        b = reactor.call(selectionKey);
                                                        if (!b) {
                                                            selectionKey.cancel();
                                                            return false;
                                                        }
                                                    }
                                            return true;
                                        }
                                );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                });
    }

    /**
     * react to the event.
     *
     * @return whether to re-use this object or remove it (and child if any) from the reactor
     */
    abstract boolean call(SelectionKey key) throws IOException, ExecutionException, InterruptedException;

    static ThreadPoolExecutor threadPool;
    Timer timer = new Timer();

    int op;

    Reactor(int op) {
        this.op = op;
    }

    static public SelectionKey registerChannel(SelectableChannel channel, ChannelController ChannelController) throws IOException {
        if (!channel.isOpen()) {
            return null;
        }
        if (channel.isBlocking()) {
            channel.configureBlocking(false);
        }
        int valid = channel.validOps();
        return channel.register(selector, valid, ChannelController);
    }

    public static ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public static Future<?> submit(Callable<?> callable) {
        return getThreadPool().submit(callable);
    }

    public static Selector getSelector() {
        return selector;
    }


    private static final ReferenceQueue<ByteBuffer> RECLAIMER = new ReferenceQueue<ByteBuffer>();

    static {

        refillBufferCache();

    }

    private static void refillBufferCache() {

        for (int pos = 0; pos < futureBufferCount; pos += BUFFSIZE) {
            cache.position(pos);
            cache.limit(pos + BUFFSIZE);
            new WeakReference<ByteBuffer>(cache.slice(), RECLAIMER);
        }

        currentBufferCount += futureBufferCount;
        futureBufferCount *= 2;
    }

    static public ByteBuffer getCacheBuffer() throws InterruptedException {
        ByteBuffer buffer;
        try {
            buffer = RECLAIMER.remove(250).get();
        } catch (InterruptedException e) {
            System.gc();
            try {
                buffer = RECLAIMER.remove(20).get();
                Logger.getAnonymousLogger().warning("System.gc() was run to reclaim buffers");
            } catch (InterruptedException e1) {
                refillBufferCache();
                synchronized (RECLAIMER) {
                    try {
                        buffer = (RECLAIMER).remove(500).get();
                        Logger.getAnonymousLogger().warning("refillBuffercache was called to add buffers");
                    } catch (InterruptedException e2) {
                        Logger.getAnonymousLogger().severe("---- buffers delayed past 770ms -- bad news. blocking");
                        long beginWait = System.currentTimeMillis();
                        buffer = RECLAIMER.remove().get();
                        long delayed = System.currentTimeMillis() - beginWait;
                        Logger.getAnonymousLogger().severe("==== buffers released at " + delayed);
                        Logger.getAnonymousLogger().info("" + currentBufferCount + " buffers total");
                    }
                }
            }
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return buffer;
    }
}