package owch;

import java.util.*;
import java.net.*;
import java.io.*;

final class NotificationFactory implements Runnable
{
    final void handleStream(InputStream istream)
	throws IOException
    {
        MetaProperties n=new Notification();
	
        n.load(istream);
	boolean more=ackPacket(n);
	if (more)
	    routePacket(n);
	
    }
    
    final boolean ackPacket(MetaProperties n)
	throws IOException
    {
	String s=(String)n.get("ACK"); //priority notification
        //check for ACK
        if(s!=null)
	    {
		Env.debug(13,"NotificationFactory.handleStream() ACK Notification: "+s);
		Env.getDatagramDispatch().remove(s);
		return false;
	    };
        if(n.getJMSReplyTo()==null)
            throw new IOException ("NotificationFactory has been sent a deformed Notification.");
	
        s=(String)n.get("JMSMessageID");
	
	try
	    {
		if(s!=null)
		    {
			URLString url=new URLString( n.getURL() );
			MetaProperties n2=new Notification();
			n2.put("ACK",s);
			String h=url.getHost();
			InetAddress dest=InetAddress.getByName(h);
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			n2.save(os);
			byte[] buf=os.toByteArray();
			//create the datagram
			DatagramPacket p=new DatagramPacket(buf,buf.length,dest,url.getPort());
			//grab an owch listener and send with it
			DatagramSocket ds=(DatagramSocket)Env.getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
		        ds.send(p); 
			return true;
		    }
	    }
	catch(SocketException e)
	    {};
	return false;
    }
    
    final void routePacket( MetaProperties n)
    {
        String s=(String)n.get("JMSMessageID");
	
	if(!recognize(n,s))
	    { 
		Env.getProxyCache().receive(n);
	    }; 
    };
    
    private boolean recognize(MetaProperties n,String s)
    {
	synchronized(recv){
	    if(recv.contains(s))
		{
		    Env.debug(12,"NotificationFactory.recognize() found DUPE Notification: "+s);
		    return
			true;
		}else
		    if(recv2.contains(s)) {
			Env.debug(12,"NotificationFactory.recognize() found DUPE Notification: "+s);
			return
			    true;
		    }
	};
	recv.add(s);
	Env.debug(12,"NotificationFactory.recognize() found NEW Notification: "+s);
	return
	    false;
 
    };

    //DoubleEndedQueue deq;
    Set recv =new HashSet();
    Set recv2=new HashSet();

    NotificationFactory()
    {
	new Thread(this).start();
    };

    
    void handleDatagram(DatagramPacket p)
    {
        ByteArrayInputStream istream=new ByteArrayInputStream(p.getData());
        try{
	    handleStream(istream);
	    istream.close();
	}catch(IOException e)
	    {
		Env.debug(7,"handleDatagram(DatagramPacket p) threw "+e.toString());
	    };
    };

    public void run(){
	try{
	    while (true){
		Thread.currentThread().sleep(60*60*10);//10 minutes
		synchronized(recv){
		    if(recv.size()>500)
			{
			    recv2=recv;
			    recv=new HashSet();
			};
		};
	    };
	}catch (Exception e)
	    {
		
	    };
    };
};

