package owch;

import java.io.*;
import java.net.*;
import java.util.*;

/** 
 * DatagramDispatch
 *
 * owch passes udp datagrams.  each of these datagrams was at one time
 * a Notification.  dpwrap is created from
 * Notification.save(ByteArrayOutputStream).  dpwrap is held in a
 * cache keyed by MessageID.
 */
final class DatagramDispatch implements Runnable
{
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

       next-in-line selection,
       alternate routing selection genome,
       system load,
       response time,

    */
    HashMap pending=new HashMap(2, 1.0f);
    HashMap tenacious=new HashMap(2, 1.0f);

    void handleDatagram(String serr, DatagramPacket p,boolean priority)
    {
        dpwrap dpw=new dpwrap(p);

        HashMap ht;

        if(priority)
            ht=pending;
        else
            ht=tenacious;

        ht.put(serr,dpw);

        try   {
		Env.debug(18,"debug: ht.put(serr,p)");
		dpw.fire();
	    }
        catch(IOException e)   {
	    };
    }
    /**
       remove packet from queue based on messageID
     */
    void remove(String serr)
    {
        Env.debug(18,"debug: remove "+serr.toString());
        tenacious.remove(serr);
        pending.remove(serr);
    }

    DatagramDispatch()
    {
        try  {
		Thread t=new Thread(this,"SocketCache");
		t.setDaemon(true);
		t.start();
	    }
        catch (Exception e)   {
	    };
    };

    public void run()
    {
        int count=0;
        while(true)
	    {
		try  {
			Thread.currentThread().sleep(2500);
		    }
		catch(InterruptedException ex) {
			Env.debug(18,"debug: DatagramDispatch.run() e "+ex);
		    }
		if(count<10000)   {
			Iterator en;
			en= pending.keySet().iterator();
			scatter(en,false);

			en = tenacious.keySet().iterator();
			scatter(en,true);
		    }
		else   {
			Env.debug(8,"DatagramDispatch table compression");
			//shrinking to promote longevity
			HashMap temp;

			temp=pending;
			pending=new HashMap(20,(float)1.0);
			compress(temp,pending);

			temp=tenacious;
			tenacious=new HashMap(20,(float)1.0);
			compress(temp,tenacious);
			count=0;
		    };
		count++;
	    };
    }

    private void compress(HashMap source, HashMap dest )
    {
        Iterator e=source.keySet().iterator();
        while(e.hasNext())   {
		Object objKey=e.next();
		Object objVal=source.get(e.next());

		if(objVal!=null)
		    dest.put(objKey,objVal);
	    };
    };

    private final void scatter(Iterator e, boolean priority)
    {
        HashMap ht;
        String dest,oadd,nadd;
        String serr;
        dpwrap dpw;
        ByteArrayInputStream bis;
        Notification n;
        URLString us;

        while(e.hasNext())
	    {
		try   {
			if(priority)
			    ht=tenacious;
			else
			    ht=pending;
			serr=(String)e.next();
			dpw=(dpwrap)ht.get(serr);
			if(dpw !=null)   {
				byte st=dpw.fire();
				Env.debug(18,"debug: DatagramDispatch.run() send " + dpwrap.age[st]+" "+serr);
				if(st==dpwrap.dead)   {
					remove(serr); 
					
					//grab the ByteInputStream
					bis=new ByteArrayInputStream(dpw.getData());

					//grab the Notification
					n=new Notification ( );
					n.load(bis);
					
					//find the Proxy
					dest=n.get("DestNode");
					
				
					Env.getProxyCache().removeProxy(dest);
				 
				    }
			    }
		    }
		catch(IOException ex)   {
			Env.debug(18,"debug: DatagramDispatch.run() e "+ex);
		    };
	    };
    };
};

