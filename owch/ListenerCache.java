package owch;

import java.util.*;

/*
 *
 * ListenerCache
 *
 */
public class ListenerCache implements java.lang.Runnable
{

    Hashtable cache=new Hashtable(7);
    //stores PortNumber->ListenerReference
    Enumeration enumCycle=null;
    boolean enumFlag;

    public Location getLocation()
    {
	//Seems done

	Integer key;
	ListenerReference lr=getNextInLine();

	//do the work of taking ls and making a Location for it
	return Location.create(lr);
    };

    //for UDP ListenerCaches this can be used to stripe the output ports of a protocol
    //such as owch
    public final ListenerReference getNextInLine()
    {
    	if(enumFlag==false)
	    {
    		enumCycle=cache.keys();
    		enumFlag=true;
	    };

    	if(enumCycle.hasMoreElements())
	    {
		return (ListenerReference)cache.get(enumCycle.nextElement());
	    }else
		{
		    //empty hashTable cannot give us good info
		    if(cache.size()==0)
    			return null;

		    //if not empty, but we're at the bottom,
		    //start over...
		    enumFlag=false;
		    return getNextInLine();
		};
    };

    public void put(ListenerReference l)
    {
	cache.put(new Integer(l.getServer().getLocalPort()),l);
	if(l.getExpiration()<lowscore)
	    resetExpire();
	enumFlag=false;
    }

    public ListenerReference remove(int port)
    {
	ListenerReference l=(ListenerReference)cache.remove( new Integer(port) );
	if(l==nextInLine)
	    resetExpire();
	enumFlag=false;
	return l;
    };

    public ListenerCache()
    {
	Thread t=new Thread(this,"ListenerCache");
	t.setDaemon(true);
	t.start();
    };

    long lowscore=0;
    ListenerReference nextInLine=null;

    public synchronized void resetExpire()
    {
        lowscore=0;
	nextInLine=null;


	//TODO: add a splaytree or something else that's sorted

	for(Enumeration  e  =  cache.keys();  e.hasMoreElements()  ;)
	    {
		ListenerReference l=(ListenerReference)cache.get(e.nextElement());

		if(l.getExpiration()==0)
		    continue;
		if(lowscore==0)
		    {
			lowscore=l.getExpiration()+100*60*10;
			//silly way of insuring a non zero value;
		    };

		if(l.getExpiration()<lowscore)
		    {
			nextInLine=l;
			lowscore=l.getExpiration();
		    };
	    };

	//on put, or remove ops
	//this routine interupts our sleeping thread with
	//the soonest available expire time,
	//and resets the sleeping value
	notify();
    };


    public synchronized void run()
    {
	while(true)
	    {
		try
		    {
			if(lowscore==0)
			    {
				wait(5000);
			    }else
				{
				    //emulate unix select() timeout
				    wait(lowscore-System.currentTimeMillis());
				};
		    }
		catch(InterruptedException e)
		    {
			continue;
		    };

		//TODO:
		//kill the nextinline's socket
		//or threadgroup
	    };
    };
};
