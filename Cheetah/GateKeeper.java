package Cheetah;

import owch.*;
import java.util.*;

import java.io.*;

import java.net.*;

/**
 * gatekeeper registers a prefix of an URL such as "/cgi-bin/foo.cgi"
 * The algorithm to locate the URL works in 2 phases;<OL>
 *
 * <LI> The weakHashMap is checked for an exact match.
 *
 * <LI> The arraycache is then checked from top to bottom to see if
 * URL startswith (element <n>) </OL>
 *
 * The when an URL is located -- registering the URL "/" is a sure
 * bet, the owch agent registered in the WeakHashMap is notified of a
 * waiting pipeline
 */
public class GateKeeper extends Node 
{
    /** references URL prefix-> NodeName */
    private WeakHashMap URLNodeMap = new WeakHashMap(384);

    /** URLSet is exported frequently after a change. */
    private Object[] arrayCache;

    /** when URLSet next used will rewrite
     * cacheArray
     */
    private boolean cacheInvalid = true;
 
    /** this is used to store the URLS in order of length */
    private SortedSet URLSet =
	new TreeSet( /** sorts strings based on length */
		    new Comparator() {
			    /** Compares its two arguments for order.
			     * In this case order is defined by strlen
			     * and then by content sorting.*/
			    public int compare(Object o1, Object o2) {
				int res= o1.toString().length() - o2.toString().length();
				if(res==0)
				    res=o1.toString().compareTo(o2.toString());//equal length objects are then copmred as strings
				return res;
			    };
			    /** Indicates whether some other object is
			     * "equal to" this Comparator. */
			    public boolean equals(Object obj) {
				return true;
			    };
			}); //holds the refetrence to url strings

    /**
     * @param to recipient owch node name
     * @param arg the text of the message
     */
    synchronized public void receive(MetaProperties notificationIn) {
        Thread.currentThread().yield();
        if (notificationIn == null) return;
        String type;
        String subJMSType;
        type = (String)notificationIn.get("JMSType");
        
        Env.debug(8, "GateKeeper - receive type = " + type);
        if (type != null) {
            String sender;
            String room;
            if (type.equals("Register")) {
                try {
		    String URLSpec=notificationIn.get("URLSpec").toString();
		    notificationIn.put("URL",notificationIn.get("URLFwd"));
		    registerURLSpec(URLSpec,notificationIn);
		    return;
                }
                catch (Exception e) {
                };
                return;
            }
        }; // if type != NULL
        super.receive( notificationIn); // superclass might know the JMSType
    };

    public GateKeeper(int port,String name,String externalHost,int  threads) {

	ListenerCache lc=new ListenerCache(); 
	try{
	    HTTPServer extSrv=new HTTPServer(port,threads) 
		{
		    public void dispatchRequest(Socket s,MetaProperties n)
		    {
			String resource=n.get ("Resource").toString();
			String method  =n.get ("Method"  ).toString();
			n.put("Proxy-Request",n.get("Request"));
			
			//1 check resource for an exact match in our WeakRefMap
			
			MetaNode l;
			
			l=(MetaNode)URLNodeMap.get(resource);
			
			
			if(l==null)
			    {
				int len=resource.length();   
			 
				if(cacheInvalid)
				    reCache();
			 
				for(int i=arrayCache.length-1 ;i>=0;i--)
				    {
					String temp=arrayCache[i].toString();
					Env.debug(500,"Pattern test on "+resource+":"+temp);
					if(temp.length()>len)
					    continue;
					if(resource.startsWith(temp)){
					    l=(MetaNode)URLNodeMap.get(temp);
					    Env.debug(500,"Pattern match on "+resource+":"+temp);
					}
				    } 
			    }
			if(l!=null)
			    {
				//3 create PipeConnection to registered location
				Env.debug(15,"GateKeeper::: creating  PipeSocket for   "+n.get("Resource").toString());
				PipeSocket p= new PipeSocket (s,l,n);
				return;
			    }; 
			//4 else super.sendFile 
			super.dispatchRequest(s,n);
			return;
		    }
		};  
	    
	    put("JMSReplyTo",   name);
	    put("ExternalHost", externalHost);
	    put("threads",      new Integer(threads));
	    put("port",         new Integer(port));
	    Env.getNodeCache().addNode(this);
	    linkTo("default");
	    
	}catch(Exception e)
	    {
		throw new Error(e.toString()+e.getMessage());
	    }
    }
    
    /** args 0=port args 1=iface 2=name 3=threads */
    static void main(String[] args) {
        int threads=8;
	int port = 8080;
        String host = "localhost";
        String name = "GateKeeper";
        System.out.println(args);
	
	if (args.length > 0)
            port = Integer.valueOf(args[0]).intValue();
	
        if (args.length > 1)
	    host=args[1];
	
        if (args.length > 2)
            name = args[2];
	
	if (args.length > 3)
            threads = Integer.valueOf(args[3]).intValue(); 
	
        GateKeeper g=new GateKeeper(port,name,host,threads);
    }
    
    /** register the beginning of a tree */
    public void registerURLSpec(String URLSpec, MetaNode l) {
	
     	synchronized(URLSet){ 
	    URLSet.add(URLSpec);
	    cacheInvalid=true;
	};
        URLNodeMap.put(URLSpec, l);
	Env.debug(15,"URL Registration:" + URLSpec +"@"+l.getJMSReplyTo( )+" -- "+l.getURL());
 
    };
    public void unregisterURLSpec(String URLSpec ) {
	synchronized(URLSet){      
	    URLSet.remove(URLSpec); 
	    cacheInvalid=true
		;	};
	Env.debug(15,"URL DeRegistration:" + URLSpec );
	
    };
    public void reCache(){
	Env.debug(150,"GateKeeper recache starting..");
	synchronized(URLSet){
	    arrayCache=URLSet.toArray();
	    cacheInvalid=false;
	    
	};
	Env.debug(150,"GateKeeper recache fin..");	
    };
};
