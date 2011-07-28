package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;

final class dpwrap implements BehaviorState {
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


  public byte fire() throws IOException {
        count++;
        DatagramSocket ds;
        if (count < lifespan) {
            ds = (DatagramSocket) Env.getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
            ds.send(p);
            return hot;
        }
        else if ((count % lifespan) == 0) //try 1/n
        {
            ds = (DatagramSocket) Env.getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
            ds.send(p);
            return cold;
        }
        else if (count > mortality) {
            if (Env.logDebug) Env.log(30, "debug:  dpwrap timeout");
            return dead;
        }
        return frozen; //don't try
    }
}
