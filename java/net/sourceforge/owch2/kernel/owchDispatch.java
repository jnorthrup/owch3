package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * owchDispatch owch passes udp datagrams.  each of these datagrams was at one time a MetaProperties.  dpwrap is created from
 * MetaProperties.save(ByteArrayOutputStream).  dpwrap is held in a cache keyed by MessageID.
 * @version $Id: owchDispatch.java,v 1.1 2002/12/08 16:05:51 grrrrr Exp $
 * @author James Northrup
 */
public final class owchDispatch implements Runnable, net.sourceforge.owch2.kernel.BehaviorState {
    Hashtable pending = new Hashtable(2, 1.0f);
    Hashtable tenacious = new Hashtable(2, 1.0f);

    public void handleDatagram(String serr, DatagramPacket p, boolean priority) {
        dpwrap dpw = new dpwrap(p);
        Hashtable ht;
        if (priority) {
            ht = pending;
        }
        else {
            ht = tenacious;
        }
        ht.put(serr, dpw);
        try {
            if (Env.logDebug) Env.log(18, "debug: ht.put(serr,p)");
            dpw.fire();
        }
        catch (IOException e) {
        }
        ;
    }

    /** remove packet from queue based on messageID */
    void remove(String serr) {
        if (Env.logDebug) Env.log(18, "debug: remove " + serr.toString());
        tenacious.remove(serr);
        pending.remove(serr);
    }

    owchDispatch() {
        try {
            Thread t = new Thread(this, "SocketCache");
            t.setDaemon(true);
            t.start();
        }
        catch (Exception e) {
        }
        ;
    };

    public void run() {
        int count = 0;
        while (!Env.shutdown) {
            try {
                Thread.currentThread().sleep(1800);
            }
            catch (InterruptedException ex) {
                if (Env.logDebug) Env.log(18, "debug: owchDispatch.run() e " + ex);
            }
            Enumeration en;
            en = pending.keys();
            scatter(en, false);
            en = tenacious.keys();
            scatter(en, true);
        }
        ;
    }

    private final void scatter(Enumeration e, boolean priority) {
        Hashtable ht;
        String dest, oadd, nadd;
        String serr;
        dpwrap dpw;
        ByteArrayInputStream bis;
        net.sourceforge.owch2.kernel.MetaProperties n;
        net.sourceforge.owch2.kernel.URLString us;
        while (e.hasMoreElements()) {
            try {
                if (priority) {
                    ht = tenacious;
                }
                else {
                    ht = pending;
                }
                serr = (String) e.nextElement();
                dpw = (dpwrap) ht.get(serr);
                if (dpw != null) {
                    byte st = dpw.fire();

                    if (st != frozen) if (Env.logDebug) Env.log(18, "debug: owchDispatch.run() send " + age[st] + " " + serr);
                    if (st == dead) {
                        remove(serr);
                    }
                }
            }
            catch (IOException ex) {
                if (Env.logDebug) Env.log(18, "debug: owchDispatch.run() e " + ex);
            }
            ;
        }
        ;
    };
}

;


