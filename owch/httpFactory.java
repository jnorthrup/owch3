package owch;

import java.net.*;
import java.io.*;
import java.util.*;


public class httpFactory implements ListenerFactory {
    public boolean readyState=false;
    boolean alive=true;

    public boolean ready() {
	return readyState;
    };
 
    Hashtable sent=new Hashtable();

    /**
     * ctor  
     */
    public httpFactory() {
    }

    public   MetaProperties getLocation() {
	return Env.getProtocolCache().getLocation("http");
    };

    public ListenerReference create(int port,int threads)
    { 
	Thread t=null;
	httpServer https;

	try{
	    https=new httpServer((int)port,threads); 
	}catch (Exception e){
	    Env.debug(2,"httpServer init failure port "+port);
	    return null;
	};
        for (int i=0;i<https.getThreads();i++) {
	    t=new Thread(https,"httpListener Thread #"+i+" / port "+https.getLocalPort());
	    t.setDaemon(true); 
	    t.start();
	};
	return https;
    };
};