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
    objects one step from   gc .

    another way of stating this is that Env holds isolated object
    graphs, no two Env caches should have interdependance.

    Protocol cache is introduced to provide a portmap control.  we
    want to be able to flush a protocol driver on the fly, close its
    socket resources, and do it by cache granularity with a single
    action context.

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
    ProxyCache, automatically.

*/


public final class Env extends Thread
{ 
    private static owchDispatch        datagramDispatch;
    private static owchFactory         socketFactory;
    private static DebugTimerOutputStream  debugTimerOutputStream=new DebugTimerOutputStream(System.out);
    private static httpFactory             httpFactory;
    private static httpRegistry            webRegistry; 
    private static Map                     formatCache;
    private static Map                     routerCache=new HashMap(13);
    private static Node                    GUINode; 
    private static NotificationFactory	   notificationFactory;
    private static ProtocolCache           protocolCache; 
    private static String                  domainName=null;
    private static boolean                 alive=false;
    private static boolean                 parentFlag=false;
    private static int                     debugLevel=500;
    private static int                     hostPort=0;
    private static int                     hostThreads=2;
    private static int                     socketCount=3;

    /**
     * returns a MetaProperties suitable for parent routing.
     *
     */
    static MetaNode domain=null;
    //TODO:
    //MetaProperties parentNode;
    private static String host=null;
    private static RouteHunter routeHunter=new RouteHunter();

    public static final int getHostPort(){
	return hostPort;
    };
    public static final void setHostPort(int port){
	 hostPort=port;
    };
    public static final int getHostThreads(){
	return hostThreads;
    };
    public static final void setHostThreads(int t){
	 hostThreads=t;
    };
    public static final int getSocketCount(){
	return socketCount;
    };
    public static final void setSocketCount(int t){
	  socketCount=t;
    };

    public final static void send(Map item){
	routeHunter.send(item);
    };

    public  final static  void unRoute(Object key){
	routeHunter.remove(key);
    };
    
    public  final static  Router getRouter(Object key){

	String className="owch."+(String)key+"Router";
	Env.debug(500,"attempting to pull up router "+className);
	Router r=(Router)routerCache.get(key);
	
	if (r==null) {
	    try{
		r=(Router)Class.forName( className).newInstance(); 
	    }catch(Exception e) {
		e.printStackTrace();
	    }
	    routerCache.put(key,r);
	} 
	return r;
    };
    
    /**
       needs work being friendlier
    */
    static public Map parseCmdLine(String args[]){
	try{
	    Notification map=new Notification();
	    
	    //harsh but effective, asume everything is key value pairs.
	    for(int i=0;i<(args.length-args.length%2);i+=2)
		{
		    if(!args[i].startsWith("-"))
			throw new Exception("Params must all start with -"); 
		    String key=args[i].substring(1);
		    String val=args[i+1]; 
		    if(key.equals("help"))
			throw new Exception ("requested help");
		    if(key.equals("h"))
			throw new Exception ("requested help"); 
		    if(key.equals("-help"))
			throw new Exception ("requested help"); 
		    if(key.equals("name"))
			key="JMSReplyTo"; 
		    //intercept a few Env specific keywords...
		    if(key.equals("Hostname"))
			setHostname(val); 
		    if(key.equals("debugLevel"))
			setDebugLevel(Integer.decode(val).intValue()); 
		    if(key.equals("HostPort"))
			setHostPort(Integer.decode(val).intValue()); 
		    if(key.equals("HostThreads"))
			setHostThreads(Integer.decode(val).intValue()); 
		    if(key.equals("SocketCount"))
			setSocketCount(Integer.decode(val).intValue()); 
		    if(key.equals("ParentURL"))
			{
			    Location l=new Location((Location)getParentNode());
			    l.put("URL",val);
			    setParentNode(l);
			    continue;
			}; 
		    if(key.equals("config"))
			{
			    FileInputStream is=new FileInputStream(val);
			    map.load(is);
			    continue;
			};
		    map.put(key,val);
		}
	    return map;
	}
	catch(Exception e){
	    e.printStackTrace();
	    cmdlineHelp();
	}
	return null;
    }
    public static final  void cmdlineHelp()
    {  
	System.out.println("All cmdline params are of the pairs form -key 'Value'\n\n "+
			   "valid environmental cmdline options are typically:\n"+
			   "-config     - config file[s] to use having (RFC822) pairs of Key: Value\n"+
			   "-JMSReplyTo - Name of agent\n"+
			   "-name       - shorthand for JMSReplyTo\n"+
			   "-Interval   - thread spin time. base number with +- 50% in milliseconds\n"+
			   "-Hostname   - a hostname or ip address to advertise in the case of several NIC's\n"+
			   "-HostPort   - port number\n"+ 
			   "-HostThreads  - Host Thread count \n"+ 
			   "-SocketCount- Multiple dynamic sockets for high load?\n"+
			   "-debugLevel - controls how much scroll is displayed\n"+
			   "-ParentURL  - typically owch://hostname:2112 -- instructs our agent host where to find an uplink\n\n"+
			   " this Edition of the parser: $Id: Env.java,v 1.2 2001/05/04 10:59:08 grrrrr Exp $\n"
			   );
	   
	System.exit(1);    return ;
    }; 

 

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
    
    static public void  sethttpRegistry( httpRegistry h)
    {
	webRegistry=h;
    };

    static public  httpRegistry gethttpRegistry  ()
    {
	
	if ( webRegistry==null){
	    webRegistry=new  httpRegistry();
	} 
	return  webRegistry; 
    };
    
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
	if(!isParentHost())
	    return new URLString( Env.getParentNode().getURL());

	return null;
    } 

    public final static ProtocolCache getProtocolCache()
    {
	if( protocolCache==null )
	    protocolCache=new ProtocolCache();
	return protocolCache;
    };

    public  final static MetaProperties getLocation(String Protocol)
    {
	Env.debug(50,"Env.getLocation - "+Protocol);
	MetaProperties l=getProtocolCache().getLocation(Protocol); 
	return l;
    };
    
    /**
     * sets the flag on the Factory Objects to act as parental sendr
     * in all final location resolution.
     *
     * @param flag sets the state to true or false
     */
    final static public void setParentHost(boolean flag)
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
     **/
    public static  final void setDebugLevel(int i) 
    {
	debugLevel=i;
    };

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
     * accessor for parental node being present in the current
     * Process.
     *
     * @return whether we are the Parent Sendr of all transactions
     */

    public final static boolean isParentHost()
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
 
    /**
     *
     * Sets the Parent Node info Object.
     *
     * @param s a MetaProperties
     */



    static final void setowchDispatch(owchDispatch s)
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

    public static final void setowchFactory(owchFactory s)
    {
	//TODO: insure that for Client Authorization Dialog, this
	//      beast doesn't start prematurely to the POOPS download
	socketFactory=s;
    }
 
 
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

    final static owchDispatch getowchDispatch()
    {
	if(datagramDispatch==null)
	    datagramDispatch=new owchDispatch();
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

    final public static owchFactory getowchFactory()
    {  
	if(socketFactory==null)    {
	    socketFactory=new owchFactory();
	};
	return socketFactory;
    }

    final public static httpFactory gethttpFactory()
    {  
	if(httpFactory==null)    {
	    httpFactory=new httpFactory();
	};
	return httpFactory;
    }
    
    final public static Object getDomainName()
    {
	if (domainName==null)
	    setDomainName(getHostname());
	
	return domainName;
    }
    final public static void  setDomainName(String dName)
    {
	domainName=dName;
    }
}
















