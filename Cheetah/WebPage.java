/*
  Deploy.java

  @author   Jim Northrup

  $Log: WebPage.java,v $
  Revision 1.2  2001/04/12 19:09:50  grrrrr
  *** empty log message ***

*/


package Cheetah;

import owch.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class WebPage extends   Node {
    
    public static void main(String[] args) { 
        String JMSReplyTo = "index.html"; 
	String 	resource = "index.html"; 

	System.out.println(args);
	if (args.length > 0)
            JMSReplyTo = args[0];  
	if (args.length >1)
            resource = args[1];
	new WebPage(JMSReplyTo,resource);
	
    };

    /*
     *  WebPage Constructor
     *
     *  Initializes communication
     * 
     *  params: name -- we name our agent anything we want..
     *           
     *  Resource -- name of a web resource
     *

     */

    public WebPage(String name,String resource) {
	put("JMSReplyTo", name);
        Env.getNodeCache().addNode(this);
        linkTo("default");

    } 

    synchronized public void receive(MetaProperties n) {
        Thread.currentThread().yield();
        if (n == null)
	    return;
        String type;
        String subJMSType;
        type = (String)n.get("JMSType"); 
        Env.debug(8, getClass().getName()+" receive type = " + type);
        if (type != null) {
            String sender;
	    /**
	       
	    incoming Message type Move

	    Host - name of a Deploy agent
	    
	    
	     */
            if (type.equals("Move")) {
                try { 
		    String host=n.get("Host").toString(); //name of a Deploy agent
		    
		    if(host==null)
			host=Env.getHostname();
		    
		    Notification n2=new Notification(); 
		    n2.put("Path","default");  
		    n2.put("Class",getClass().getName());
		    n2.put("JMSDestination", host); 
		    
                }
                catch (Exception e) {
		    Env.debug(8, getClass().getName()+" receive error" + e.getMessage());
		    e.printStackTrace();
                };
                return;
            }
        }; // if type != NULL
        super.receive(n); // superclass might know the JMSType
    }; // end handle MetaProperties 
};
