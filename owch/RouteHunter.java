package owch;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * @version $Id: RouteHunter.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
public class   RouteHunter{
    public void remove(Object key)
    {
	Router router=getInnerMost();  
	do{ 
	    Env.debug(500,"***"+router.getClass().getName()+" killing "+key); 
	    router.remove(key);
	    router=router.getNextOutbound ();
	}while(router!=null); 
    }
    public Router getOuterMost() {
	return Env.getRouter (Env.isParentHost()? "Domain":"owch");
    }
    public Router getInnerMost() {
	return Env.getRouter ("IPC"); 
    };
    public void send(Map item){
	if(item.get("JMSReplyTo")==null){
	    Env.debug(500,"*** dropping nameless message");
	    return;
	}
	boolean sated=false;
	Router router=getInnerMost();  
	do{ 
	    Env.debug(500,"***"+router.getClass().getName()+" testing "+item.toString()+"");
	    Object dest=router.getDestination(item); 
	    sated=router.hasElement(dest);
	    
	    if(sated)
		router.send(item);
	    else
		router=router.getNextOutbound ();
	    
	}while(!sated&&router!=null); 
	
	router=getOuterMost();
	
	do{ 
	    sated=router.addElement(item);
	    router=router.getNextInbound ();
	}while(!sated&&router!=null); 
    } 
};
