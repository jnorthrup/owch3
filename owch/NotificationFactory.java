package owch;

import java.util.*;
import java.net.*;
import java.io.*;

final class NotificationFactory
{
     final void handleStream(InputStream istream)
     throws IOException
     {
        String s;
        Notification n=new Notification();

        n.load(istream);
        s=(String)n.get("ACK"); //priority notification
        //check for ACK
        if(s!=null)
        {
            Env.debug(13,"NotificationFactory.handleStream() ACK Notification: "+s);
            Env.getDatagramDispatch().remove(s);
            return;
        };

        if(n.getNodeName()==null)
            throw new IOException ("NotificationFactory has been sent a deformed Notification.");

        if(Env.getDebugLevel()>12)
            n.save(System.err);

        s=(String)n.get("SerialNo");

        if(s!=null)
        {
            try
            {
                if(!recognize(n,s))
                {
                  Env.getProxyCache().addQueue(n);
                };
                //code borrowed from proxy.h perverted from DatagramDispatch

                URLString url=new URLString(n.getURL());

                Notification n2=new Notification();
                n2.put("ACK",s);

    		    //String()
    		    //ok, we think java DNS sucks rocks.
    		    //we use ip #'s for the time being.
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
            }
            catch(SocketException e)
            {}
            catch(IOException e)
            {}
        }
    }

synchronized private boolean recognize(Notification n,String s)
{
    if(recv.containsKey(s))
    {
        Env.debug(12,"NotificationFactory.recognize() found DUPE Notification: "+s);
        return
            true;
    };
    recv.put(s,n);
    Env.debug(12,"NotificationFactory.recognize() found SER Notification: "+s);
    return
        false;
};

    // DoubleEndedQueue deq;
    Hashtable recv=new Hashtable(1000,.4f);

    NotificationFactory()
    {
    };

    void handleDatagram(DatagramPacket p)
    {
        ByteArrayInputStream istream=new ByteArrayInputStream(p.getData());

        try
        {
            handleStream(istream);
			istream.close();
        }catch(IOException e)
        {
            Env.debug(7,"handleDatagram(DatagramPacket p) threw "+e.toString());
        };
    };

};

