package owch;

import java.util.*;
import java.lang.reflect.*;


/*
 *
 * ProtocolCache
 *
 */

/**
 * @version $Id: ProtocolCache.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup 
 */
public class ProtocolCache extends HashMap
{
    public ListenerCache getListenerCache(String Protocol) {
	Env.debug(20,"protocolCache.getListenerCache -- "+Protocol);
	ListenerCache lc=(ListenerCache)get(Protocol);
	if(lc==null) {
	    try{  
		Env.debug(20,"attempting to create "+Protocol);
		String cname= "owch."+Protocol;
		String factory = Protocol+"Factory";
	    	Env.debug(20,"attempting to register "+ factory);
		lc=new ListenerCache(); 
		for(int i=0; i< Env.getSocketCount(); i++)
		    {
			Method m1=Env.class.getMethod( "get"+ factory, new Class[]{} );
			ListenerFactory lf=(owch.ListenerFactory)m1.invoke( this, new Object[]{});
		; 
		lc.put(	lf.create(i==0? Env.getHostPort():0 ,Env.getHostThreads()));
		    } 
		put(Protocol,lc);
	    }
	    catch(Exception e){
		e.printStackTrace();
	    };
	};
	return lc; 
    };
    
    public Location getLocation(String Protocol)
    {
	ListenerCache l=(ListenerCache)getListenerCache(Protocol);

	if(l!=null) {
		return l.getLocation();
	    };
 
	return null;
    };
}

