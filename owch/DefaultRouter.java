package owch;

import java.net.*;
import java.io.*;
import java.util.*;

public class DefaultRouter implements Router {
    public void remove(Object key){};
    public Object getDestination(Map item){
	return item.get("JMSDestination");
    };
    
    public boolean hasElement(Object key){ 
	if(Env.isParentHost() )
	    {
		Env.debug(12,"Domain is dropping packet for "+key);
		return false;
	    }
	return true;
    };
    public Set getPool(){
	return new TreeSet();
    };
   public  Router getNextOutbound (){
	return null;
    }
   public  Router getNextInbound () 
    {
	return Env.getRouter (Env.isParentHost()? "Domain":"owch" ); 
    };
    public boolean addElement(java.util.Map item)
    {
	return false;
    };
    
    public void send(Map item){  
	if(Env.isParentHost())//TODO: implement soft Reference queueuing
	    {
		return;
	    }
	Router r=Env.getRouter("owch");
	item.put("URL",Env.getParentNode().getURL());
	r.send(item);
    };
};
