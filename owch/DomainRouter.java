package owch;

import java.net.*;
import java.io.*;
import java.util.*;

public class DomainRouter implements  Router { 

    private Map elements=new TreeMap();

    public void remove(Object key){
	elements.remove(key);
    };

    public Object getDestination(Map item){
	return item.get("Domain-Gateway");
    };
    
    public Set getPool(){
	return elements.keySet();
    };
    
    public Router getNextOutbound (){
	return Env.getRouter (Env.isParentHost()?null:"Default"); 
    };

    public Router getNextInbound () 
    {
	return Env.getRouter ("owch");
    };

    public boolean addElement(Map item){
	try{
	    MetaProperties mp=new Location();
	    mp.put("JMSReplyTo",item.get("JMSReplyTo").toString());//looks like joe@joedomain
	    mp.put("Domain-Gateway",item.get("Domain-Gateway").toString());//looks like "joedomain"
	    elements.put(item.get("JMSReplyTo"),mp);
	    return true;
	}catch( Exception e)//null ptr exceptions are hoped for..
	    {
	    }
	return false;
    };
    public boolean hasElement(Object key){
	return elements.containsKey(key);
    };
    
    public void send(Map item){
	Router r=getNextInbound ();
	Map domain=(Map)elements.get(item.get("JMSDestination"));               //looks like joe@joedomain
	Object dest=domain.get("JMSReplyTo");                                 //looks like "bob"
	item.put("JMSReplyTo",item.get("JMSReplyTo")+"@"+Env.getDomainName());//looks like bob@bobdomain
	item.put("JMSDestination",dest);                                      //looks like joe
	item.put("Domain-Gateway",Env.getDomainName());                       //looks like bobdomain
	r.send(item);
    };
};
