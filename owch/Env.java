package owch;
import java.io.*;
import java.util.*;

import java.net.*;
  
/*
    Env class:
    mobile agent host environment;

    intent:
    this resembles the master object factory

    Env holds onto the live objects, holds onto the routing caches,
    holds onto the kernel socket objects, etc.

    Semantics for Env components are such that we wish to keep all
    objects one step from recurisve gc, to this effect, our object
    references should be cache deep, one level, and decoupled from
    binary component linkages between riding agents.

    another way of stating this is that Env holds isolated object
    graphs, no two Env caches should have interdependance.

    Protocol cache is introduced to provide an iron fisted portmap
    control.  we want to be able to flush a protocol driver on the
    fly, close its socket resources, and do it by cache granularity
    with a single action context.

    Node/Proxy/Socket Caches are namespace active reference enumerations.

    Mobile Agent Hosting:

    Env needs to manages Node references in such a way that other
    protocols ride thru granting RMI protocol access to stream
    objects, and code, without pre-binding.

    OOPS is proposed to house agent persistance, via an oops wrapper
    to a stream protocol with which to pipe RMI and retain a namespace
    reference.

    Notifications passing URL contents differing from owch need to be
    addressed for interaction pipes.  notification factory expects a
    stream, not an socket.  This streaming compensation needs to
    reflect an ProtocolCache entry, its listeners, and its filters.

    to propose a streaming agent interface, a listener can be
    invented:

    the listener pattern is as follows: listener.accept().  grab a
    stream filter reference granting MetaProperties format.  send to
    NotificationFactory.

    this sends an owch MetaProperties Notification.

    If the Mobile agent can speak owch, it will be found in nodeCache.
    The notification URL will define the transport context of the owch
    tunnel by advertising an URL.

    when a Proxy sends, it will add the listener reference of the
    current Env, in its protocol context (URL determined).

    this means that the Notification carries the URL into the
    ProxyCache, automatically, and its kinda proven.

*/


public final class Env extends Thread
{ 
    private final static int debugLevel=500;
    private static DatagramDispatch    datagramDispatch;
    private static DatagramFactory  socketFactory;
    private static DebugTimerOutputStream debugTimerOutputStream=new DebugTimerOutputStream(System.out);
    private static HTTPFactory httpFactory;
    private static Node GUINode;
    private static NodeCache nodeCache;
    private static NotificationFactory	notificationFactory;
    private static ProtocolCache protocolCache;
    private static ProxyCache proxyCache;
    private static boolean parentFlag=false;
    private static boolean alive=false;
    private static java.util.Map formatCache;
    /**
     * returns a MetaProperties suitable for parent routing.
     *
     */
    static MetaNode domain=null;
    //TODO:
    //MetaProperties parentNode;
    private static String host=null;

    static public String getHostname()
    {
	try{
	    if (host==null){
		
		String ip= InetAddress.getLocalHost().toString();
		return  ip.substring(ip.indexOf('/')+1);
	    }
	}catch( java.net.UnknownHostException e){
	};
	return host;
	
    }
    
    static public void setHostname(String h)
    {
	host=h;
    };

    public final static Format getFormat (String name )
    { 
	return (Format) getFormatCache().get(name);
    };
    
    public final static  void registerFormat (String name,Format f)
    { 
	getFormatCache().put(name,f);
	Env.debug(100 , "Registering Formatter: "+name);
    };
    private final static Map getFormatCache()
    {
	if(formatCache==null)
	    formatCache=new TreeMap();
	return formatCache;
    };


    public final static DebugTimerOutputStream getDebugStream()
    {

        return (DebugTimerOutputStream) debugTimerOutputStream;
    }
    public final static URLString getDefaultURL()
    {
	if(!isParentNode())
	    return new URLString(getProxyCache().getProxy("default").getURL());

	return null;
    }
    


    public final static ProtocolCache getProtocolCache()
    {
	if(protocolCache==null)
	    protocolCache=new ProtocolCache();
	return protocolCache;
    };

    public  final static MetaProperties getLocation(String Protocol)
    {
	MetaProperties l=getProtocolCache().getLocation(Protocol);

	if(l==null)
	    {
		if(Protocol.equals("owch"))
		    {
			ListenerCache listenerCache=new ListenerCache();

			ListenerFactory lf=getDatagramFactory();

			//this should make life interesting
			listenerCache.put(lf.create(0,2));
			listenerCache.put(lf.create(0,2));
			listenerCache.put(lf.create(0,2));
			listenerCache.put(lf.create(0,2));

			Env.getProtocolCache().put("owch",listenerCache);
			return getLocation("owch");
		    }
	    };
	return l;
    };

    /**
     *
     * sets the flag on the Factory Objects to act as parental sendr in all final location resolution.
     *
     * @param flag sets the state to true or false
     */
    final static public void setParentNode(boolean flag)
    {
	parentFlag=flag;
    }

    final static public void setParentNode(MetaNode l)
    {
	domain=(MetaNode)l;
    }


    /**
     * Debug Level Accessor
     *
     * @param i debug level
     *
     *
     public final void setDebugLevel(int i)
     {
     debugLevel=i;
     };*/

    /**
     * Debug Level Accessor
     *
     * @return the level of debug output verbosity
     *
     */
    public final static int getDebugLevel()
    {
	return debugLevel;
    }
    /**
     * accessor for parental node being present in the current Process.
     *
     * @return whether we are the Parent Sendr of all transactions
     */

    public final static boolean isParentNode()
    {
	return parentFlag;
    }


    
    /**
     *
     * waits an interval averageing 5 seconds.
     *
     */
    
    private synchronized  final static void wait5()
    {
	Env.debug(20,"debug: wait5 begin");

	try
	    {
		Thread.currentThread().sleep(5000,0);
	    }
	catch(Exception e)
	    {

	    }
	Env.debug(20,"debug: wait5 end");
    }

    public void main(String[] args)
    {
    }

    public Env()
    {
	if(alive==true)return;
	dorun();
    }

    synchronized private void dorun()
    {
	start();
	try
	    {
		sleep(1000);
	    }catch(InterruptedException e)
		{

		};

    };
    /**
     * sends logfile spew if the debugLevel is set high enough for an individual message.
     *
     * @param lev an int of varying verboseness specification
     * @param s the text to log.
     */


    public static final void debug(int lev,String s)
    {
	if(debugLevel>=lev)
	    debugTimerOutputStream.println(s);
    } ;
    static final void setNodeCache(NodeCache s)
    {
	nodeCache=s;
    };

    /**
     *
     * Sets the Parent Node info Object.
     *
     * @param s a MetaProperties
     */



    static final void setDatagramDispatch(DatagramDispatch s)
    {
	datagramDispatch=s;
    };

    static final void setNotificationFactory(NotificationFactory s)
    {
	notificationFactory=s;
    };
    /**
     * sets the process's ServerSocket provider Env.
     *
     * @param s New SocketEnv.
     */

    public static final void setDatagramFactory(DatagramFactory s)
    {
	//TODO: insure that for Client Authorization Dialog, this
	//      beast doesn't start prematurely to the POOPS download
	socketFactory=s;
    }

    static final void setProxyCache(ProxyCache s)
    {
	proxyCache=s;
    }

    public synchronized final static NodeCache getNodeCache()
    {
	if(nodeCache==null)
	    nodeCache=new NodeCache();
	return nodeCache;
    };

    public final static MetaNode getParentNode()
    {
	//TODO:  This oughta become System.Property stuff.  (shrug)

	if(domain==null)
	    {
		Location l=new Location();
		l.put("Created",  "env.getDomain()" );
		l.put("JMSReplyTo", "default" ); 
		l.put("URL",      "owch://localhost:2112");
		setParentNode(l);
	    }; 
	return domain;
    }

    final static DatagramDispatch			getDatagramDispatch()
    {
	if(datagramDispatch==null)
	    datagramDispatch=new DatagramDispatch();
	return datagramDispatch;
    }

    final static NotificationFactory getNotificationFactory()
    {
	if(notificationFactory==null)
	    {
		notificationFactory=new NotificationFactory();
	    }
	return notificationFactory;
    };

    final public static DatagramFactory getDatagramFactory()
    {  
	if(socketFactory==null)    {
	    socketFactory=new DatagramFactory();
	};
	return socketFactory;
    }

    final public static HTTPFactory getHTTPFactory()
    {  
	if(httpFactory==null)    {
	    httpFactory=new HTTPFactory();
	};
	return httpFactory;
    }

    /**
     * returns the current Factory default ProxyCache.
     *
     */
    public final static ProxyCache getProxyCache()
    {
	if(proxyCache==null)
	    proxyCache=new ProxyCache();
	return proxyCache;
    };


}
















