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
		Env.getowchDispatch().remove(s);
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
		Env.send(n);
	    }; 
    };
    
    private boolean   recognize(MetaProperties n,String s)
    {
	boolean res=false;
	java.lang.ref.SoftReference ref;
	synchronized(recv){

	for(Iterator i=recv.iterator();i.hasNext();)   {
		Object o=i.next();
		ref=(java.lang.ref.SoftReference)o;
		String prev=(String)ref.get();
		if(prev==null)
		    continue;
		
		if(prev.equals(s)){
		    res=true;
		    break;
		};
	    };
	};
	return res;
    };

    //DoubleEndedQueue deq;
    int trigger=0;
    Set recv =new HashSet();
    java.lang.ref.ReferenceQueue q=new java.lang.ref.ReferenceQueue();

    NotificationFactory()
    {
	
	Thread t=new Thread();
	t.setDaemon(true);
	t.start();
    };
    
    void handleDatagram(DatagramPacket p)
    {
        ByteArrayInputStream istream=new ByteArrayInputStream(p.getData());
        try{
	    handleStream(istream);
	    istream.close();
	}catch(IOException e)
	    {
		Env.debug(7,"handleowch(DatagramPacket p) threw "+e.toString());
	    };
    };

    public void run(){
	try{
	java.lang.ref.Reference ref;
	    while (true){
		ref=q.remove(3000L);//3 seconds
		if(ref!=null)
		    synchronized(recv){ 
			recv.remove(ref);
			Env.debug(40,getClass().getName()+"::collecting softref ---- ");
		    };
	    }
	    
	}catch (Exception e)
	    {
		e.printStackTrace();
		throw new Error("NotificationFactory lost");
	    };
    };
}
 
