package owch;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * DatagramDispatch owch passes udp datagrams.  each of these datagrams was at one time
 * a MetaProperties.  dpwrap is created from MetaProperties.save(ByteArrayOutputStream).  dpwrap is held in a
 * cache keyed by MessageID.
 */
final class DatagramDispatch implements Runnable {

    final byte hot = 0;
    final byte cold = 1;
    final byte frozen = 2;
    final byte dead = 3;
    final int lifespan = 12;
    final int mortality = lifespan * lifespan;
    final static String[] age =
        {
            "hot",
            "cold",
            "frozen",
            "dead"
        };

    /* TODO:
       define metrics of storage,
       do self profiling to determine optimal algorithm for

       agent threshold collections for behavior expression
       {
       claim tired,
       claim scared,
       claim wounded,
       claim endangered,
       claim expired,
       claim dead

       },
       cache genomes
       {
       (having
       enumeration,
       add by key,
       get by key,
       remove by key,
       methods
       )
       lifespan,
       container type
       {
       hashtable,
       splaytree,
       avl,
       binarysort
       },

       spool direction rank functions
       {
       serial,
       unit hit count
       {
       hi,
       lo
       },
       age
       {
       mru,
       lru
       },
       }
       }

       selection behavior genome
       {
       /n/ genome count selection criterion comprising
       [any but]
       {
       default,
       peer -- implicitly the same application object type,
       random from the cache,
       mru,
       lru
       }
       }

       proxy node cache
       {(per node metrics)
       hop check,
       ping time,
       peer flag
       }

       nextElement-in-line selection,
       alternate routing selection genome,
       system load,
       response time,

    */

    Hashtable pending = new Hashtable(2, 1.0f);
    Hashtable tenacious = new Hashtable(2, 1.0f);

    void handleDatagram(String serr, DatagramPacket p, boolean priority) {
        dpwrap dpw = new dpwrap(p);
        Hashtable ht;
        if (priority)
            ht = pending;
        else
            ht = tenacious;
        ht.put(serr, dpw);
        try {
            Env.debug(18, "debug: ht.put(serr,p)");
            dpw.fire();
        }
        catch (IOException e) {
        };
    }

    /** remove packet from queue based on messageID */
    void remove(String serr) {
        Env.debug(18, "debug: remove " + serr.toString());
        tenacious.remove(serr);
        pending.remove(serr);
    }

    DatagramDispatch() {
        try {
            Thread t = new Thread(this, "SocketCache");
            t.setDaemon(true);
            t.start();
        }
        catch (Exception e) {
        };
    };

    public void run() {
        int count = 0;
        while (true) {
            try {
                Thread.currentThread().sleep(1800);
            }
            catch (InterruptedException ex) {
                Env.debug(18, "debug: DatagramDispatch.run() e " + ex);
            }
            Enumeration en;
            en = pending.keys();
            scatter(en, false);
            en = tenacious.keys();
            scatter(en, true);
        };
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
                if (priority)
                    ht = tenacious;
                else
                    ht = pending;
                serr = (String)e.nextElement();
                dpw = (dpwrap)ht.get(serr);
                if (dpw != null) {
                    byte st = dpw.fire();
                    Env.debug(18, "debug: DatagramDispatch.run() send " + age[st] + " " + serr);
		    if (st==cold){
			//by now the proxy must've aged. rewrap with present proxy info
			remove(serr);
                        //grab the ByteInputStream
                        bis = new ByteArrayInputStream(dpw.getData());
                        //grab the MetaProperties
                        n = new Notification();
                        n.load(bis);
                        //find the Proxy
                        dest = n.get("JMSDestination").toString();
			if(n.get("Cold")==null)
			    {
				n.put("Cold","t");
				
			    }
			else
			    Env.getProxyCache().removeProxy(dest);
			Env.getProxyCache().receive(n); 
		    }
                    if (st == dead) {
                        remove(serr);
                        //grab the ByteInputStream
                        bis = new ByteArrayInputStream(dpw.getData());
                        //grab the MetaProperties
                        n = new Notification();
                        n.load(bis);
                        //find the Proxy
                        dest = n.get("JMSDestination").toString();
                        Env.getProxyCache().removeProxy(dest);
                    }
                }
            }
            catch (IOException ex) {
                Env.debug(18, "debug: DatagramDispatch.run() e " + ex);
            };
        };
    };


    final class dpwrap {
        DatagramPacket p;
        int count = 0;

        dpwrap(DatagramPacket p_) {
            p = p_;
        };

        final byte[] getData() {
            return p.getData();
        };

        final InetAddress getAddress() {
            return p.getAddress();
        };

        final int getPort() {
            return p.getPort();
        };

        //TODO:
        //      Set up interface for fire() to use function objects
        //      and build a heterogenous collection of function objects to determine fireing interval, destination, lifespan, and hops.
        //
        //
        public byte fire() throws IOException {
            count++;
            DatagramSocket ds = (DatagramSocket)Env.getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
            if (count < lifespan) {
                ds.send(p);
                return hot;
            }
            else if ((count % lifespan) == 0) //try 1/n
            {
                ds.send(p);
                return cold;
            }
            else if (count > mortality) {
                owch.Env.debug(30, "debug:  dpwrap timeout");
                return dead;
            };
            return frozen; //don't try
        };
    };
};
