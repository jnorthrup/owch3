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
public class GateKeeper extends Node implements javax.naming.directory.DirContext 
{ 
    public static void main(String[] args) { 
	Map m=Env.parseCmdLine(args);
	
	if(!(m.containsKey("JMSReplyTo")&&m.containsKey("HostPort") ))
	    {
		System.out.println(
				   "\n\n******************** cmdline syntax error\n"+
				   "GateKeeper Agent usage:\n\n"+
				   "-name name\n"+ 
				   "-HostPort port\n"+  
				   "$Id: GateKeeper.java,v 1.3 2001/05/04 10:59:08 grrrrr Exp $\n" 
				   );
		System.exit(2);
	    };
	GateKeeper d=new GateKeeper(m );
 
    };
    
    
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
		    String Item=notificationIn.get("URLSpec").toString();
		    notificationIn.put("URL",notificationIn.get("URLFwd"));
		    Env.gethttpRegistry().registerItem(Item,notificationIn);
		    return;
                }
                catch (Exception e) {
                };
                return;
            }  ;
	    if (type.equals("UnRegister")) {
                try {
		    String Item=notificationIn.get("URLSpec").toString();
		    notificationIn.put("URL",notificationIn.get("URLFwd"));
		    Env.gethttpRegistry().unregisterItem(Item );
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
       service on the agent host and handling messages to marshal http
       registrations

     */
    public GateKeeper(Map m) {
	super(m);
	Env.getLocation("http");
    }
    
  
};
