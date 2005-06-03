package net.sourceforge.owch2.kernel;

import static net.sourceforge.owch2.kernel.BehaviorState.*;

import java.io.*;
import java.net.*;

final class dpwrap {
    DatagramPacket p;
    int count = 0;

    dpwrap(DatagramPacket p_) {
        p = p_;
    }

    final byte[] getData() {
        return p.getData();
    }

    final InetAddress getAddress() {
        return p.getAddress();
    }

    final int getPort() {
        return p.getPort();
    }

    public BehaviorState fire() throws IOException {
        count++;
        DatagramSocket ds;
//        final Env env = Env.getInstance();
//        if (env.logDebug) Logger.global.info("protocolCache.getListenerCache -- " + proto);
//        ListenerCache lc = (ListenerCache) get(proto);
//        if (lc == null) {
//            if (proto == ProtocolType.owch) {
//                final int hostThreads = env.getHostThreads();
//                final int owchPort = env.getOwchPort();
//                final InetAddress hostAddress = env.getHostAddress();
//                final owchFactory instance = owchFactory.getInstance();
//                lc.put(instance.create(hostAddress, (owchInit) ? 0 : owchPort, hostThreads));
//                owchInit = true;
//            }
//            if (proto == ProtocolType.Http) {
//                final int hostThreads = env.getHostThreads();
//                final int httpPort = env.getHttpPort();
//                final InetAddress hostAddress = env.getHostAddress();
//                final httpFactory instance = httpFactory.getInstance();
//                lc.put(instance.create(hostAddress, (httpInit) ? 0 : httpPort, hostThreads));
//                httpInit = true;
//            }
//            if (proto == ProtocolType.Pipe) {
//                final int hostThreads = env.getHostThreads();
//                final int pipePort = env.getPipePort();
//                final InetAddress hostAddress = env.getHostAddress();
//                final PipeFactory instance = PipeFactory.getInstance();
//                lc.put(instance.create(hostAddress, (pipeInit) ? 0 : pipePort, hostThreads));
//                pipeInit = true;
//            }
//        }
        ListenerCache listenerCache = ProtocolType.owch .ListenerCacheInstance();
        if (count < lifespan) {
            ds = (DatagramSocket) listenerCache.getNextInLine().getServer();
            ds.send(p);
            return hot;
        } else {
            if ((count % lifespan) == 0) {
                ds = (DatagramSocket) listenerCache.getNextInLine().getServer();
                ds.send(p);
                return cold;
            } else {
                if (count > mortality) {
                    return dead;
                } else {
                    return frozen;
                }
            }


        }
    }
}
