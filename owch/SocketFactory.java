package owch;

import java.net.*;
import java.io.*;
import java.util.*;


public final class SocketFactory implements ListenerFactory {
    public boolean readyState=false;
    boolean alive=true;

    public boolean ready() {
	return readyState;
    };
 
    Hashtable sent=new Hashtable();

    /**
     * ctor
     *
     *
     */
    public SocketFactory() {
    }

    public final Location getLocation() {
	return Env.getProtocolCache().getLocation("owch");
    };


    public ListenerReference create(int port,int threads)
    { 
	Thread t=null;
	owchListener udps=null; 
	try{
	    udps=new owchListener((int)port);
	}
	catch(Exception e){
	    Env.debug(2,"owchListener failure on port "+port);
	};
        for (int i=0;i<udps.getThreads();i++) {
	    t=new Thread(udps,"owchListener Thread #"+i+" / port "+udps.getLocalPort());
	    t.setDaemon(true); 
	    t.start();
	};
	return udps;
    };
};
