package owch;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * @version $Id: DefaultRouter.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
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
