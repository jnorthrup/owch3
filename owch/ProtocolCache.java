package owch;

import java.util.*;


/*
 *
 * ProtocolCache
 *
 */
public class ProtocolCache
{

    Hashtable cache=new Hashtable(7);
    //Protocol -> ListenerCache

    public ListenerCache getListenerCache(String Protocol) {
	ListenerCache lc=(ListenerCache)cache.get(Protocol);
	if(lc==null) {
		if(Protocol.equals("owch")) {
			lc=new ListenerCache();
			lc.put(Env.getDatagramFactory().create(0,2));
			lc.put(Env.getDatagramFactory().create(0,2));
			lc.put(Env.getDatagramFactory().create(0,2));
			put("owch",lc);
		    };
		if(Protocol.equals("http")) {
			lc=new ListenerCache();
			lc.put(Env.getHTTPFactory().create(0,2));
			lc.put(Env.getHTTPFactory().create(0,2));
			lc.put(Env.getHTTPFactory().create(0,2));
			put("http",lc);
		    };
	
	    };
	return lc; 
    };

    public void put(String Protocol, ListenerCache lc) {
	cache.put(Protocol,lc);
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

