/*
 * Tester.java
 *************************************************************************************

  Created by Jim Northrup

  Modified by   EDS CSTS
  @version     3.0 1/23/97

  Tester implementation.  This object tests network agents.


*************************************************************************************/

package Cheetah;

import owch.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Tester extends Node   {
   
    public static void main(String[] args) {
        String host = "localhost";
        int port = 2112;
        String JMSReplyTo = "Tester";
        System.out.println(args);
        if (args.length > 2)
            host = args[2];
        if (args.length > 1)
            port = Integer.valueOf(args[1]).intValue();
        if (args.length > 0)
            JMSReplyTo = args[0];
        new Tester(JMSReplyTo);
    };

    /*
     *  Client Constructor
     *
     *  Initializes communication
     */

    public Tester(String name) {
        put("JMSReplyTo", name); 
        linkTo("Main");
        MetaProperties n = new Notification();
        n.put("JMSDestination", "Main");
        n.put("JMSType", "Test");
        send(n);
	n = new Notification();
        n.put("JMSDestination", "GateKeeper");
        n.put("JMSType", "Register");
	n.put("URLSpec", "/rnodi_owch.jar");
	n.put("URLFwd", Env.getProtocolCache().getLocation("http").getURL()); 
        send(n);
 
	n = new Notification();
        n.put("JMSDestination", "GateKeeper");
        n.put("JMSType", "Register");
	n.put("URLSpec", "/");
	n.put("URLFwd", Env.getProtocolCache().getLocation("http").getURL()); 
        send(n);

	n = new Notification();
        n.put("JMSDestination", "Quick");
        n.put("JMSType", "Clone");
	n.put("Host", "host_lx");
        send(n);

        while (true) {
            try {
                Thread.currentThread().sleep(200000);
            }
            catch (Exception e) { };
        }
    }; //end construct
 

    /**
     * @param to recipient owch node name
     * @param arg the text of the message
     */
    synchronized public void receive(MetaProperties n) {
        Thread.currentThread().yield();
        if (n == null) return;
        String type;
        String subJMSType;
        type = (String)n.get("JMSType"); 
        Env.debug(8, "Client - receive type = " + type);
        if (type != null) {
            String sender;
            String room;
            if (type.equals("Test")) {
                try {
                    URL p1 = new URL(n.get("Path").toString());
                    URLClassLoader loader = new URLClassLoader(
							       new URL[] { p1 });
                    loader.loadClass(n.get("Class").toString()).newInstance();
                }
                catch (Exception e) {
                };
                return;
            }; // end class 
	}; // if type != NULL
	super.receive(n); // superclass might know the JMSType
    }; // end handle MetaProperties
}; // end class
