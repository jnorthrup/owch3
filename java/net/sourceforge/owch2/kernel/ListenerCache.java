package net.sourceforge.owch2.kernel;

import java.util.*;

/**
 * ListenerCache
 *
 * @author James Northrup
 * @version $Id$
 */
public class ListenerCache implements Runnable {
    Map<Integer, ListenerReference> cache = new TreeMap<Integer, ListenerReference>();
    Iterator<Integer> enumCycle = null;
    boolean enumFlag;

    public Location<? extends String> getLocation() {
        //Seems done
        ListenerReference lr = getNextInLine();
        //do the work of taking ls and making a Location for it
        return Location.create(lr);
    }

    //for UDP ListenerCaches this can be used to stripe the output ports of a protocol
    //such as owch
    public ListenerReference getNextInLine() {
        if (!enumFlag) {
            enumCycle = cache.keySet().iterator();
            enumFlag = true;
        }
        if (enumCycle.hasNext()) {
            return cache.get(enumCycle.next());
        } else {
            //empty hashTable cannot give us good info
            if (cache.size() == 0) {
                return null;
            }
            //if not empty, but we're at the bottom,
            //start over...
            enumFlag = false;
        }
        return getNextInLine();
    }


    public void put(ListenerReference l) {
        cache.put(l.getServer().getLocalPort(), l);
        if (l.getExpiration() < lowscore) {
            resetExpire();
        }
        enumFlag = false;
    }

    public ListenerReference remove(int port) {
        ListenerReference l = cache.remove(port);
        if (l == nextInLine) {
            resetExpire();
        }
        enumFlag = false;
        return l;
    }


    public ListenerCache() {
        Thread t = new Thread(this, "ListenerCache");
        t.setDaemon(true);
        t.start();
    }

    long lowscore = 0;
    ListenerReference nextInLine = null;

    public void resetExpire() {
        synchronized (this) {
            lowscore = 0;
            nextInLine = null;

            for (Object key : cache.keySet()) {

                ListenerReference l = cache.get(key);
                if (l.getExpiration() == 0) {
                    continue;
                }
                if (lowscore == 0) {
                    lowscore = l.getExpiration() + 100 * 60 * 10;
                    //silly way of insuring a non zero value;
                }
                if (l.getExpiration() < lowscore) {
                    nextInLine = l;
                    lowscore = l.getExpiration();
                }
            }
            //on put, or remove ops
            //this routine interupts our sleeping thread with
            //the soonest available expire time,
            //and resets the sleeping value
            notifyAll();
        }
    }


    public void run() {
        synchronized (this) {
            while (!Env.getInstance().shutdown) {
                try {
                    if (lowscore == 0) {
                        wait(5000);
                    } else {
                        //emulate unix select() timeout
                        wait(lowscore - System.currentTimeMillis());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();  //!TODO: review for fit
                }
            }
        }
    }
}


