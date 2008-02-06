package net.sourceforge.owch2.kernel;

import java.io.*;
import java.lang.ref.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import static java.nio.channels.SelectionKey.*;
import java.util.*;
import java.util.concurrent.*;


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
@SuppressWarnings({"unchecked"})
public class Reactor {

    private Selector selector;

    //    private int bufferSize = 1024 << 4;
    private int bufferCount = 128;
    private int bufferStep[];
    private int currentBufferCount = 0;
    private boolean shutdown;
    private static Reactor instance;
    private Map<SelectionKey, Exchanger> keyXMap = new WeakHashMap<SelectionKey, Exchanger>();
    private static final int KILOBYTE = 2 << 9;
    private Map<Integer, ReferenceQueue<ByteBuffer>> reclaimer = new ConcurrentHashMap<Integer, ReferenceQueue<ByteBuffer>>();


    static Reactor getInstance() {
        if (instance == null) {
            instance = new Reactor();
        }
        return instance;
    }

    Reactor() {

        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
//        cache = ByteBuffer.allocateDirect(bufferCount * bufferSize);

        bufferStep = new int[16];
        Arrays.fill(bufferStep, bufferCount);

        threadPool.submit(new Runnable() {
            public void run() {
                while (selector.isOpen()) {
                    try {
                        if (selector.select() < 1) continue;

                    } catch (IOException e) {
                        e.printStackTrace();  //Todo: verify
                        continue;

                    }
                    Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                    for (final SelectionKey key : selectionKeySet) {
                        try {
                            int rops = key.readyOps();

                            final Object payload = key.attachment();
                            final Exchanger exchanger = keyXMap.get(key);
                            if (exchanger == null) key.cancel();

                            if ((key.isAcceptable() || key.isReadable())) {
                                channelInduction(key, exchanger);
                            } else {
                                if (key.isReadable()) {
                                    if (payload instanceof Exchanger) {
                                        Exchanger e = (Exchanger) payload;
                                        spinRead(key, null, e);
                                    } else if (payload instanceof Exchanger[]) {
                                        Exchanger[] e = (Exchanger[]) payload;
                                        spinRead(key, null, e);
                                    } else if (payload instanceof Integer) {
                                        spinRead(key, ((Integer) payload), exchanger);
                                    } else if (payload instanceof int[]) {
                                        //array of ByteBuffer sizes for Scatter/Gather
                                        throw new IllegalArgumentException("Scatter Gather not yet implemented");
                                    } else {
                                        spinRead(key, null, exchanger);
                                    }
                                }
                                if (0 < (rops | OP_WRITE)) {
                                    if (payload instanceof Exchanger) {
                                        Exchanger e = (Exchanger) payload;
                                        spinWrite(key, null, e);
                                    } else if (payload instanceof Exchanger[]) {
                                        Exchanger[] e = (Exchanger[]) payload;
                                        spinWrite(key, null, e);
                                    } else if (payload instanceof Integer) {
                                        spinWrite(key, ((Integer) payload), exchanger);
                                    } else if (payload instanceof OwchTransport) {
                                        DatagramChannel d = (DatagramChannel) key.channel();
                                        OwchTransport t = (OwchTransport) payload;
                                        final OwchTransport.Quad<Transaction, CharSequence, SocketAddress, ByteBuffer> charSequenceSocketAddressByteBufferQuad = t.sendX.exchange(null);
                                        final int i2 = d.write(charSequenceSocketAddressByteBufferQuad.getObject());
                                    } else if (payload instanceof int[]) {
                                        //array of ByteBuffer sizes for Scatter/Gather
                                        throw new IllegalArgumentException("Scatter Gather not yet implemented");
                                    } else {
                                        spinWrite(key, null, exchanger);
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();  //Todo: verify

                        }
                    }
                }
            }
        }

        );
    }

    private void channelInduction(SelectionKey key, Exchanger exchanger) throws IOException, InterruptedException {
        if (key.isAcceptable()) {


            ServerSocketChannel sc = (ServerSocketChannel) key.channel();
            SocketChannel ic;
            while (null != (ic = sc.accept()))
                exchanger.exchange(ic);
        } else if (key.isConnectable()) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            if (socketChannel.finishConnect()) {
                SocketChannel reboundChannel = (SocketChannel) exchanger.exchange(socketChannel);
                if (reboundChannel != null && reboundChannel.isOpen()) {
                    ((SocketChannel) reboundChannel.configureBlocking(false)).connect(socketChannel.socket().getRemoteSocketAddress());
                    Exchanger exchanger1 = registerChannel(reboundChannel, exchanger, reboundChannel, OP_CONNECT);
                }
                SocketAddress socketAddress = socketChannel.socket().getRemoteSocketAddress();
            }

        }
    }

    private void performIo(SelectionKey key, int rops, Object payload, Exchanger exchanger) {
        if (key.isReadable()) {
            if (payload instanceof Exchanger) {
                Exchanger e = (Exchanger) payload;
                spinRead(key, null, e);
            } else if (payload instanceof Exchanger[]) {
                Exchanger[] e = (Exchanger[]) payload;
                spinRead(key, null, e);
            } else if (payload instanceof Integer) {
                spinRead(key, ((Integer) payload), exchanger);
            } else if (payload instanceof int[]) {
                //array of ByteBuffer sizes for Scatter/Gather
                throw new IllegalArgumentException("Scatter Gather not yet implemented");
            } else {
                spinRead(key, null, exchanger);
            }
        }
        if (0 < (rops | OP_WRITE)) {
            if (payload instanceof Exchanger) {
                Exchanger e = (Exchanger) payload;
                spinWrite(key, null, e);
            } else if (payload instanceof Exchanger[]) {
                Exchanger[] e = (Exchanger[]) payload;
                spinWrite(key, null, e);
            } else if (payload instanceof Integer) {
                spinWrite(key, ((Integer) payload), exchanger);
            } else if (payload instanceof int[]) {
                //array of ByteBuffer sizes for Scatter/Gather
                throw new IllegalArgumentException("Scatter Gather not yet implemented");
            } else {
                spinWrite(key, null, exchanger);
            }

        }
    }

    private Future<Integer> spinRead(final SelectionKey key, final Integer exponent, final Exchanger... e) {
        return (Future<Integer>) submit(new Callable<Integer>() {
            public Integer call() throws IOException, InterruptedException {
                ByteBuffer buffer = exponent == null ? getCacheBuffer() : getCacheBuffer(exponent);
                int bytes = 0;
                int bytes2 = 0;
                while ((bytes = ((ByteChannel) key.channel()).read(buffer)) > 0) {
                    bytes2 += bytes;
                    buffer = (ByteBuffer) ((ByteBuffer) e[0].exchange(buffer)).clear();
                }

                return bytes2;
            }
        });
    }


    private Future<Integer> spinWrite(final SelectionKey key, final Integer exponent, final Exchanger... e) {
        return (Future<Integer>) submit(new Callable<Integer>() {
            public Integer call() throws IOException, InterruptedException {
                ByteBuffer buffer = exponent == null ? getCacheBuffer() : getCacheBuffer(exponent);
                int bytes = 0;
                int bytes2 = 0;


                do {
                    bytes2 += bytes;
                    buffer = (ByteBuffer) e[0].exchange(buffer);
                } while (0 < (bytes = ((ByteChannel) key.channel()).write(buffer)));

                return bytes2;
            }
        });
    }


    /**
     * react to the event.
     *
     * @return whether to re-use this object or remove it (and child if any) from the reactor
     */
//     boolean call(SelectionKey key) throws IOException, ExecutionException, InterruptedException;

    private ThreadPoolExecutor threadPool;
    private Timer timer = new Timer();
//
//    public SelectionKey registerChannel(SelectableChannel channel, ChannelController ChannelController) throws IOException {
//        if (!channel.isOpen()) {
//            return null;
//        }
//        if (channel.isBlocking()) {
//            channel.configureBlocking(false);
//        }
//        int valid = channel.validOps();
//        return channel.register(selector, valid, ChannelController);
//    }


    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    /**
     * Establish Exchanger chain for sockets and files
     *
     * @param channel
     * @param callback
     * @param interest
     * @return
     * @throws ClosedChannelException
     */
    public Exchanger registerChannel(SelectableChannel channel, Exchanger callback, Object payload, int... interest) throws ClosedChannelException {
        int inter = 0;
        for (int i2 : interest)
            inter = i2;

        if (callback == null)
            callback = new Exchanger<SelectableChannel>();


        SelectionKey selectionKey = channel.register(selector, inter == 0 ? channel.validOps() : inter, payload == null ? callback : payload);

        keyXMap.put(selectionKey, callback);

        return callback;
    }

    public Selector getSelector() {
        return selector;
    }


    private void refillBufferCache() {
        refillBufferCache(4);
    }

    private void refillBufferCache(int power) {
        final int bufferSize = KILOBYTE << power;
        final ByteBuffer cache = ByteBuffer.allocateDirect(bufferSize * bufferStep[power]);

        if (!reclaimer.containsKey(power)) reclaimer.put(power, new ReferenceQueue<ByteBuffer>());
        for (int pos = 0; pos < bufferStep[power]; pos += bufferSize) {
            cache.position(pos);
            cache.limit(pos + bufferSize);
            {
                new WeakReference<ByteBuffer>(cache.slice(), reclaimer.get(power));
            }

        }
        bufferStep[power] *= 2;
    }

    public ByteBuffer getCacheBuffer() {
        return getCacheBuffer(4);
    }

    private ByteBuffer getCacheBuffer(int power) {
        ByteBuffer buffer = null;
        try {
            buffer = (ByteBuffer) reclaimer.get(power).remove(250).get();
        } catch (InterruptedException e) {
            refillBufferCache();
            return getCacheBuffer(power);
        }
        return buffer;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public int getBufferCount() {
        return bufferCount;
    }

    public int getCurrentBufferCount() {
        return currentBufferCount;
    }

    public void setCurrentBufferCount(int currentBufferCount) {
        this.currentBufferCount = currentBufferCount;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public static void setInstance(Reactor instance) {
        Reactor.instance = instance;
    }

    public Map<SelectionKey, Exchanger> getKeyXMap() {
        return keyXMap;
    }

    public void setKeyXMap(Map<SelectionKey, Exchanger> keyXMap) {
        this.keyXMap = keyXMap;
    }


    public Timer getTimer() {
        return timer;
    }

    public Future<?> submit(Callable<?> c) {
        return getThreadPool().submit(c);
    }

    public Future<?> submit(Runnable runnable) {
        return getThreadPool().submit(runnable);
    }

    /**
     * this is a shortcut for a buffer-exchanger chain
     *
     * @param circleX
     * @return
     */
    public Future submit(final Exchanger... circleX) {
        return submit(getCacheBuffer(), circleX);
    }

    /**
     * wrapper for weak reference submit.
     * if/when one exchanger is garbage collected or is interrupted, the activities end.
     *
     * @param circleX
     * @return a future<null>
     */
    public Future submit(Object seed, final Exchanger... circleX) {

        final Reference<Exchanger>[] wX = (Reference<Exchanger>[]) new WeakReference[circleX.length];

        for (int i = 0; i < circleX.length; i++) {
            Exchanger exchanger = circleX[i];
            wX[i] = new WeakReference<Exchanger>(exchanger);
        }
        return submit(seed, (Reference<Exchanger>[]) wX);

    }

    /**
     * this is a shortcut for a buffer-exchanger chain
     *
     * @param circleX
     * @return
     */
    public Future submit(final Reference<Exchanger>... circleX) {
        return submit(getCacheBuffer(), circleX);
    }

    /**
     * this passes a common <e.g. Buffer> along a chain of exchangers and
     * circles the <e.g. buffer> back to the start.
     * <p/>
     * if/when one exchanger is garbage collected or is interrupted, the activities end.
     *
     * @param circleX
     * @return a future<null>
     */
    public Future submit(final Object seed, final Reference<Exchanger>... circleX) {
        final Runnable r = new Runnable() {
            public void run() {
                Object o = seed;
                try {
                    do
                        for (Reference<Exchanger> exchangerWeakReference : circleX) {
                            if (exchangerWeakReference.isEnqueued())
                                return;
                            final Exchanger exchanger = exchangerWeakReference.get();
                            o = exchanger.exchange(o);
                        } while (true);
                } catch (InterruptedException e) {
                }
            }
        };
        return submit(r);
    }
}