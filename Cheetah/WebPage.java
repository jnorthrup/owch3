/*
  Deploy.java

  @author   Jim Northrup

  $Log: WebPage.java,v $
  Revision 1.1  2001/04/12 19:07:26  grrrrr
  Initial revision

*/


package Cheetah;

import owch.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class WebPage extends   Node {
    
    public static void main(String[] args) {
        String host = "localhost";
        int port = 2112;
        String JMSReplyTo = "index.html"; 

	System.out.println(args);
        if (args.length > 2)
            host = args[2];
        if (args.length > 1)
            port = Integer.valueOf(args[1]).intValue();
        if (args.length > 0)
            JMSReplyTo = args[0];
        new WebPage(JMSReplyTo,"index.html");
    };

    /*
     *  Client Constructor
     *
     *  Initializes communication
     */

    public WebPage(String name,String resource) {
	put("JMSReplyTo", name);
        Env.getNodeCache().addNode(this);
        linkTo("default");

    } 

    synchronized public void receive(MetaProperties n) {
        Thread.currentThread().yield();
        if (n == null) return;
        String type;
        String subJMSType;
        type = (String)n.get("JMSType"); 
        Env.debug(8, getClass().getName()+" receive type = " + type);
        if (type != null) {
            String sender;
            String room;
            if (type.equals("Move")) {
                try { 
		    String host=n.get("Host").toString(); 
		    if(host==null)
			host=Env.getHostname();

		    Notification n2=new Notification();
		    
		    n2.put("Path","default");  
		    
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
