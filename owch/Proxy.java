package owch;

import java.io.*;
import java.util.*;
import java.net.*;

public class Proxy extends TreeMap implements MetaNode  
{
    //Used to float nodes while they aren't visible
    protected DoubleEndedQueue queue;


    public Proxy()
    {
        queue=new DoubleEndedQueue();
    }

    public Proxy(MetaNode  p)
    {
	put("JMSReplyTo",p.getJMSReplyTo());
	put("URL",p.getURL()); 
        queue=new DoubleEndedQueue();
    }

    public void addQueue(MetaProperties n1){
        MetaProperties n=new Notification(n1);  //typesafe our cast
        Env.debug(12,"(Proxy)"+getJMSReplyTo()+".addQueue(n)");

        Date d=new Date();

        String serr=n.getJMSReplyTo()+":"+n.get("JMSDestination").toString()+":"+n.get("JMSType").toString()+"["+d.toString()+"] "+ser++;
        serr="(-: "+serr+"."+serr.hashCode()+" ;-)";
        n.put("JMSMessageID",serr);
 
        n.put("URL",Env.getLocation("owch").getURL());

	try {
	    String u=getURL();
	    if ( u==null)
		if(Env.isParentNode()){
		    Env.debug(2,"******Domain:  DROPPING PACKET FOR "+getJMSReplyTo());
		    return;
		}
		else 
		    u=Env.getParentNode().getURL();
	    URLString url=new URLString(u);
	    String h=url.getHost();
	    InetAddress dest=InetAddress.getByName(h);
	    ByteArrayOutputStream os=new ByteArrayOutputStream();
	    n.save(os);
	    byte[]buf=os.toByteArray();
	    DatagramPacket p=new DatagramPacket(buf,buf.length,dest,url.getPort());
	    Env.getDatagramDispatch().handleDatagram(serr, p, n.get("Priority")!=null);
	}
	catch(Exception e) {
		Env.debug(5,"debug: addQueue(n) threw a "+e.toString());
		e.printStackTrace(); 
	};
    }
    
    static int ser;
    
    boolean ready=true;
    synchronized private void runq()
    {

        while(true)
	    {
		Thread.currentThread().yield();
		try{
		    ready=true;
		    wait();
		    ready=false;
		}catch(InterruptedException e)
		    {
		    };
		ready=false;

		Node node=Env.getNodeCache().getNode(getJMSReplyTo());
		int retry=0;
		try
		    {
			//more ugly borrowing
			MetaProperties n=(MetaProperties)queue.pop();
			if(n==null)
			    continue;
		    }
		catch(Exception e)
		    {
			Env.debug(
				  10,"debug: Proxy.runq() shutting down for "+getJMSReplyTo()+".");
		    };
	    };
    }

    /**
     *
     * waits an interval averageing 5 seconds.
     *
     */

    private synchronized  final static void wait5()
    {
        Env.debug(20,"debug: wait5 begin");

        try
	    {
		Thread.currentThread().sleep(3000,0);
	    }
        catch(Exception e)
	    {

	    }
        Env.debug(20,"debug: Proxy.wait5 end");
    };

    public final String getURL()
    {
        String s=(String)get("URL");
        return s;
    }
 
    public final String getJMSReplyTo()
    {
        return (String)get("JMSReplyTo");
    };
};


