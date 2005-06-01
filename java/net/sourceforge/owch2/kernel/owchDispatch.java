package net.sourceforge.owch2.kernel;

import static net.sourceforge.owch2.kernel.BehaviorState.dead;
import static net.sourceforge.owch2.kernel.BehaviorState.frozen;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * owchDispatch owch passes udp datagrams.  each of these datagrams was at one time a MetaProperties.  dpwrap is created from
 * MetaProperties.save(ByteArrayOutputStream).  dpwrap is held in a cache keyed by MessageID.
 *
 * @author James Northrup
 * @version $Id: owchDispatch.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */

public final class owchDispatch implements Runnable {
//    BehaviorState x;

    Hashtable pending = new Hashtable(2, 1.0f);
    Hashtable tenacious = new Hashtable(2, 1.0f);
    private double[] age;

    public void handleDatagram(String serr, DatagramPacket p, boolean priority) {
        dpwrap dpw = new dpwrap(p);
        Hashtable ht;
        if (priority) {
            ht = pending;
        } else {
            ht = tenacious;
        }
        ht.put(serr, dpw);
        try {
            if (Env.getInstance().logDebug)
                Env.getInstance().log(18, "debug: ht.put(serr,p)");
            dpw.fire();
        }
        catch (IOException e) {
        }
        ;
    }

    /**
     * remove packet from queue based on messageID
     */
    void remove(String serr) {
        if (Env.getInstance().logDebug) Env.getInstance().log(18, "debug: remove " + serr.toString());
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
    }

    public void run() {
        int count = 0;
        while (!Env.getInstance().shutdown) {
            try {
                sleep(1800);
            }
            catch (InterruptedException ex) {
                if (Env.getInstance().logDebug) Env.getInstance().log(18, "debug: owchDispatch.run() e " + ex);
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
        MetaProperties n;
        URLString us;
        while (e.hasMoreElements()) {
            try {
                if (priority) {
                    ht = tenacious;
                } else {
                    ht = pending;
                }
                serr = (String) e.nextElement();
                dpw = (dpwrap) ht.get(serr);
                if (dpw != null) {
                    BehaviorState st = dpw.fire();

                    if (st != frozen)
                    if (Env.getInstance().logDebug)
                        Env.getInstance().log(18, "debug: owchDispatch.run() send " + st.toString() + " " + serr);
                    if (st == dead) {
                        remove(serr);
                    }
                }
            }
            catch (IOException ex) {
                if (Env.getInstance().logDebug) Env.getInstance().log(18, "debug: owchDispatch.run() e " + ex);
            }
            ;
        }
        ;
    }

    ;
}

;


