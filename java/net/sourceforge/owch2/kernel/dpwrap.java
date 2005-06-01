package net.sourceforge.owch2.kernel;

import static net.sourceforge.owch2.kernel.BehaviorState.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
        if (count < lifespan) {
            ds = (DatagramSocket) Env.getInstance().getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
            ds.send(p);
            return hot;
        } else {
            if ((count % lifespan) == 0) {
                ds = (DatagramSocket) Env.getInstance().getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
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
