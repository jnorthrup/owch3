package owch;

import java.io.*;
import java.util.*;
import java.net.*;

public class Proxy extends MetaProperties
{
    //Used to float nodes while they aren't visible
    protected DoubleEndedQueue queue;

    public Proxy(MetaProperties p)
    {
        super(p);
        queue=new DoubleEndedQueue();
    }

    public void addQueue(Notification n1)
    {
        Notification n=new Notification(n1);  //typesafe our cast
        Env.debug(12,"(Proxy)"+getNodeName()+".addQueue(n)");

        Date d=new Date();

        String serr=n.getNodeName()+"["+d.toString()+"] "+ser++;
        serr="(-: "+serr+"."+serr.hashCode()+" ;-)";
        n.put("SerialNo",serr);
        n.put("URL",Env.getLocation("owch").getURL());


	    //String()
	    //ok, we think java DNS sucks rocks.
	    //we use ip #'s for the time being.
	    try
	    {
            URLString url=new URLString(getURL());
            String h=url.getHost();
            InetAddress dest=InetAddress.getByName(h);
    	    ByteArrayOutputStream os=new ByteArrayOutputStream();
	        n.save(os);
    	    byte[]buf=os.toByteArray();
    	    DatagramPacket p=new DatagramPacket(buf,buf.length,dest,url.getPort());
    	    Env.getDatagramDispatch().handleDatagram(serr, p, n.get("Priority")!=null);
	    }
	    catch(Exception e)
	    {
	        Env.debug(5,"debug: addQueue(n) threw a "+e.toString());
	        e.printStackTrace();
            throw new Error("Proxy("+getNodeName()+").addQueue(n) threw a "+e.toString());
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

            Node node=Env.getNodeCache().getNode(getNodeName());
            int retry=0;
            try
            {
                //more ugly borrowing
                Notification n=(Notification)queue.pop();
                if(n==null)
						continue;
            }
            catch(Exception e)
            {
                 Env.debug(
						10,"debug: Proxy.runq() shutting down for "+getNodeName()+".");
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


};


