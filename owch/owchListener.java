package owch;

import java.net.*;
import java.io.*;

/*
 * owchListener.java
 * 
 */
class owchListener extends UDPServerWrapper implements  Runnable,ListenerReference {
    owchListener(int port)throws  java.net.SocketException{
	super(port);
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
	return 2;
    }
    public ServerWrapper getServer(){
	return this;
    };

    public void expire(){	
	getServer().close();
    };

}; 







