package owch;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * @version $Id: owchRouter.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
public class owchRouter implements Router { 
    static long ser=0;
    private Map elements=new TreeMap();
     public void remove(Object key){elements.remove(key);};
    public Object getDestination(Map item){
	return item.get("JMSDestination");
    };  
    public Set getPool(){
	return elements.keySet();
    };
    public boolean hasElement(Object key){
	return elements.containsKey(key);
    };   
    public Router getNextOutbound (){ 
	return  Env.getRouter (Env.isParentHost()? "Domain":"Default"); 
    };
    public Router getNextInbound () {
	    return Env.getRouter ("IPC");
    };
        
    public boolean addElement(Map item){
	try{
	    Location met=new Location();
	    met.put("JMSReplyTo",item.get("JMSReplyTo").toString());
	    met.put("URL",item.get("URL").toString());
	    elements.put(item.get("JMSReplyTo"), met);
	    return true;
	}catch( Exception e)//null ptr exceptions are hoped for..
	    {
	    }
	return false;
    };
 
    public void send(Map item)
    {
	Notification n=new Notification(item);
	if(n.getJMSReplyTo()==null) 
	    return;
	Date d=new Date();
	String serr=n.get("JMSReplyTo")+":"+n.get("JMSDestination").toString()+":"+n.get("JMSType").toString()+"["+d.toString()+"] "+ser++;
	n.put("JMSMessageID",serr);
 
        n.put("URL",Env.getLocation("owch").getURL());
	MetaProperties prox=( MetaProperties)elements.get(n.get("JMSDestination"));
	if(prox==null)
	    prox=(MetaProperties)Env.getParentNode();
	
	String u=prox.get("URL").toString();
	try { 
	    if ( u==null)
		if(Env.isParentHost()){
		    Env.debug(2,"******Domain:  DROPPING PACKET FOR "+prox.get("JMSReplyTo"));
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
	    Env.getowchDispatch().handleDatagram(serr, p, n.get("Priority")!=null);
	}
	catch(Exception e) {
		Env.debug(5," owch.send(n) threw a "+e.toString());
		e.printStackTrace(); 
	}; 
    };

};
