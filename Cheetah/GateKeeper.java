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
            if (type.equals("Register")) {
                try {
		    String URLSpec=notificationIn.get("URLSpec").toString();
		    notificationIn.put("URL",notificationIn.get("URLFwd"));
		    Env.getHTTPRegistry().registerURLSpec(URLSpec,notificationIn);
		    return;
                }
                catch (Exception e) {
                };
                return;
            }  ;
	    if (type.equals("UnRegister")) {
                try {
		    String URLSpec=notificationIn.get("URLSpec").toString();
		    notificationIn.put("URL",notificationIn.get("URLFwd"));
		    Env.getHTTPRegistry().unregisterURLSpec(URLSpec );
		    return;
                }
                catch (Exception e) {
                };
                return;
            }
        }; // if type != NULL
        super.receive( notificationIn); // superclass might know the JMSType
    };
    
    /**
       this has the effect of taking over the command of the http
       service on the agent host and handling messages to marshal HTTP
       registrations

     */
    public GateKeeper(int port,String name,String externalHost,int  threads) {
 	try{
	    ListenerCache lc=new ListenerCache(); 
	    lc.put(Env.getHTTPFactory().create(port,threads)); 
	    Env.getProtocolCache().put("http",lc);
	    Env.setHostname(externalHost);
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
     
};
