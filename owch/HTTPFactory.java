package owch;

import java.net.*;
import java.io.*;
import java.util.*;


public class HTTPFactory implements ListenerFactory {
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
    public HTTPFactory() {
    }

    public final MetaProperties getLocation() {
	return Env.getProtocolCache().getLocation("http");
    };

    public ListenerReference create(int port,int threads)
    { 
	Thread t=null;
	HTTPServer https;
	try{
	    https=new HTTPServer((int)port); 
	}catch (Exception e){
	    Env.debug(2,"HTTPServer init failure port "+port);
	    return null;
	};
        for (int i=0;i<https.getThreads();i++) {
	    t=new Thread(https,"HTTPListener Thread #"+i+" / port "+https.getLocalPort());
	    t.setDaemon(true); 
	    t.start();
	};
	return https;
    };
};
