/*
 * ProxyCache.java
 *************************************************************************************

  Created by Jim Northrup

  Modified by EDS CSTS
  @version 2.0 12/15/96


*************************************************************************************/
package owch;

import java.io.*;
import java.net.*;
import java.util.*;

final public class ProxyCache
{

    public void removeProxy(String name)
    {
	nameProxyMap.remove(name);
    }

    public void add(Proxy proxy)
    {
        nameProxyMap.put(proxy.getNodeName(),proxy);
	Env.debug(200,"debug: ProxyCache.add"+proxy.getNodeName());
	proxy.put("creation",String.valueOf(System.currentTimeMillis()));
    }
    DoubleEndedQueue outbound;

    /**
     *  gives a listing of all contained proxy names.
     */

    public String enumProxies()
    {
        String s="";

        for (Enumeration e = nameProxyMap.keys() ; e.hasMoreElements() ;)
	    {
		s=s+e.nextElement()+" ";
	    };
        return s;
    }

    ProxyCache( )
    {
        nameProxyMap=new Hashtable();
        outbound=new DoubleEndedQueue();
    }

    static int ser=0;

    synchronized public void addQueue(Notification n)
    {
	Env.debug(18,"ProxyCache.Handler(MetaProperties) entered");

	if(n==null)
	    {
		throw new Error("debug:  null sent to ProxyCache.handleNotification");
	    }

        if(Env.getDebugLevel()>=12)
	    {
		try{
		    n.save(System.err);
		}catch(IOException e){};
	    }
        else
	    Env.debug(6,"ProxyCache.Handler(MetaProperties) recvd from "+n.getNodeName());

        String sender=n.getNodeName();
        Env.debug(8,"ProxyCache - Message recieved from sender =" + sender);

        if(sender==null)
	    {
		Env.debug(5,"debug: ProxyCache.Handler(MetaProperties) sender null in notification.");
		return;
	    }

        Node origin=Env.getNodeCache().getNode(sender);

        //update sender's proxy info.
        if(origin==null)
	    {
		Location l=new Location(n);
		l.put("Created","ProxyCache.handleNotification()");
		if(n.get("ResentFrom")==null)
		    getProxy(l);

	    }else
		{

		    //check to see if we're reading broadcast stuff
		    if(n.get("ResentFrom")!=null)
			{
			    Node node_=(owch.Node)Env.getNodeCache().getNode((String)n.get("DestNode"));
			    if(node_!=null)node_.handleNotification(new Notification(n));
			    return;
			};

		    //give it a serial#
		    String serr=(String)n.get("SerialNo");
		    if(serr==null)
			{
			    Date d=new Date();
			    serr=sender+"["+d.toString()+"] "+ser;
			    n.put("SerialNo","(-: "+serr+"."+serr.hashCode()+" ;-)");
			    ser++;
			};

		};

        //if its not us, add it to the cache
        String t=(String)n.get("DestNode");

        if(t==null)
	    {
		t="";
	    };

        StringTokenizer st = new StringTokenizer(t);
        String d;
        Node nod;
        Proxy proxy;
        while (st.hasMoreTokens())
	    {
		d=st.nextToken();
		n.put("DestNode",d);

		if(origin==null)
		    {
			//test NodeCache
			nod=Env.getNodeCache().getNode((String)d);

			if(nod!=null)
			    {
				//send
				nod.handleNotification(new Notification(n));
				continue;
			    };
		    }

		proxy=(Proxy)nameProxyMap.get(d);

		if(proxy!=null)
		    {
			proxy.addQueue(new Notification(n));
			continue;
		    }

		//Create Proxy
		Location l=new Location();
		l.put("Created","ProxyCache.handleNotification()2");
		l.put("NodeName",d);
		getProxy(l).addQueue(new Notification(n));
	    }
    }

    Proxy getProxy(String name)
    {
        Location l=new Location();
        l.put("NodeName",name);
        return getProxy(l);
    }

    Proxy getProxy(Location location)
    {
        if(location==null)
	    {
		Env.debug(5,"debug: ProxyCache.Handler(Location) was sent a null Location; ");
		return null;
	    }

        //the objective is to merge proxies
        String nm=location.getNodeName();

        if(nm==null)
	    {
		Env.debug(5,"debug: ProxyCache.Handler(Location)  was sent a Location without a NodeName");
		return null;
	    }

        //merge q's
        Proxy p=null;
        String lu=location.getURL();
        String pu=null;


        //TODO: differentiate owch protocol from http, ftp, etc.

        p=(Proxy)nameProxyMap.get(nm);

        if(p==null)
	    {
		//create anew
		p=new Proxy(location);
		nameProxyMap.put(p.getNodeName(),p);
		Env.debug(10,"debug: ProxyCache.Handler(Location) created proxy for "+p.getNodeName()+".");
	    }
        //update URL

        pu=p.getURL();

        if(lu!=null)
	    {
		//do not update if its the same
		if(pu!=null)
		    {
			if(!(lu.equals(pu)))
			    {
				p.put("URL",lu);
				Env.debug(10,"debug: ProxyCache.Handler(Location) updated proxy for "+p.getNodeName()+".");
			    }
		    }else
			{
			    p.put("URL",lu);
			};
	    }else
		if(pu==null)
		    {
			if(!Env.isParentNode())
			    {
				String tu=((MetaProperties)nameProxyMap.get("default")).getURL();
				if(tu!=null)
				    p.put("URL",tu);
			    };
		    };

        return p;
    }

    Hashtable nameProxyMap;
}
