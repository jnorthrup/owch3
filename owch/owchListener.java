package owch;

import java.net.*;
import java.io.*;

/*
 * owchListener.java
 * 
 */

/**
 * @version $Id: owchListener.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
class owchListener extends UDPServerWrapper implements  Runnable,ListenerReference {
    int threads;
    owchListener(int port,int threads)throws  java.net.SocketException{

	super(port);
	this.threads=threads;
    };

    public final void run() {
	Env.debug(20,"debug: "+Thread.currentThread().getName()+" init");
	NotificationFactory nf=Env.getNotificationFactory();
	byte[] bar=new byte[32768];  //overkill on purpose
	while(true) {
	    try {
		DatagramPacket p=new DatagramPacket(bar,bar.length);
		receive(p);
		nf.handleDatagram(p);
		Env.debug(12,"debug: spin, "+Thread.currentThread().getName());
	    }
	    catch(IOException e) {
		Env.debug(5,"debug: OWCH RUN BREAK");
		break;
	    };
	};
	Env.debug(5,"debug: OWCH THREAD STOP");
    };

    public String getProtocol(){
	return "owch";
    };
    public long getExpiration() {
	return (long) 0;
    }
    public int getThreads(){
	return this.threads;
    }

    public ServerWrapper getServer(){
	return this;
    };

    public void expire(){	
	getServer().close();
    };

}; 







