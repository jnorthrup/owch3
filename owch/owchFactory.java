package owch;

import java.net.*;
import java.io.*;
import java.util.*;


public final class owchFactory implements ListenerFactory {
    public boolean readyState=false;
    boolean alive=true;

    public boolean ready() {
	return readyState;
    };
 
    HashMap sent=new HashMap();

    /**
     * ctor
     *
     *
     */
    public owchFactory() {
		    Env.debug(200,"DataGramFactory instantiated");
    }

    public final MetaProperties getLocation() {
	return Env.getProtocolCache().getLocation("owch");
    };


    public ListenerReference create(int port,int threads)
    { 
	Thread t=null;
	owchListener udps=null; 
	try{
	    udps=new owchListener( port,threads);
	}
	catch(Exception e){
	    Env.debug(2,"owchListener failure on port "+port);
	    e.printStackTrace();
	};
        for (int i=0;i<Env.getHostThreads();i++)
	    {
		t=new Thread(udps,
			     "owchListener Thread #"
			     +i
			     +" / port "
			     +udps.getLocalPort());
		t.setDaemon(true); 
		t.start();
	    };
	return udps;
    };
};
