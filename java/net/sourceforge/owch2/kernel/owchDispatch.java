package net.sourceforge.owch2.kernel;

import static net.sourceforge.owch2.kernel.BehaviorState.*;

import java.io.*;
import static java.lang.Thread.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * owchDispatch owch passes udp datagrams.  each of these datagrams was at one time a MetaProperties.  dpwrap is created from
 * MetaProperties.save(ByteArrayOutputStream).  dpwrap is held in a cache keyed by MessageID.
 *
 * @author James Northrup
 * @version $Id: owchDispatch.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */

public final class owchDispatch implements Runnable {
//    BehaviorState x;

    Hashtable pending = new Hashtable(2, 1.0f);
    Hashtable tenacious = new Hashtable(2, 1.0f);

    private static owchDispatch instance;

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
            if (false)
                Logger.getAnonymousLogger().info("debug: ht.put(serr,p)");
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
        if (false) Logger.getAnonymousLogger().info("debug: remove " + serr.toString());
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
        while (!Env.getInstance().shutdown) {
            try {
                sleep(1800);
            }
            catch (InterruptedException ex) {
                if (false) Logger.getAnonymousLogger().info("debug: owchDispatch.run() e " + ex);
            }
            Enumeration en;
            en = pending.keys();
            scatter(en, false);
            en = tenacious.keys();
            scatter(en, true);
        }
    }

    private final void scatter(Enumeration e, boolean priority) {
        Hashtable ht;
        String serr;
        dpwrap dpw;
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
                        if (false)
                            Logger.getAnonymousLogger().info("debug: owchDispatch.run() send " + st.toString() + " " + serr);
                    if (st == dead) {
                        remove(serr);
                    }
                }
            }
            catch (IOException ex) {
                if (false) Logger.getAnonymousLogger().info("debug: owchDispatch.run() e " + ex);
            }
        }
    }


    public static owchDispatch getInstance() {
        if (instance == null) instance = new owchDispatch();
        return instance;
    }
}

