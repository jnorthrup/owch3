/*
 * Client.java
 *************************************************************************************

  Created by Jim Northrup

  Modified by   EDS CSTS
  @version     3.0 1/23/97

  Client implementation.  This object is responsible for handling
  communication with Room, and Domain servers.


*************************************************************************************/

package Cheetah;

import owch.*; 
import java.io.*;
import java.net.*;
import java.util.*;

public class Client extends Node 
{
    public static void main(String[] args) { 
	Map m=Env.parseCmdLine(args);
	
	if(!(m.containsKey("JMSReplyTo")  ))
	    {
		System.out.println(
				   "\n\n******************** cmdline syntax error\n"+
				   "Client Agent usage:\n\n"+
				   "-name name\n"+  
				   
				   "$Id: Client.java,v 1.2 2001/05/04 10:59:08 grrrrr Exp $\n"
 				   );
		System.exit(2);
	    };
	Client d=new Client(m );
    }
    
    /*
     *  Client Constructor
     *
     *  Initializes communication 
     */
	
    public Client(Map m)
    { 
	super(m); 
	linkTo("Main");

	MetaProperties n=new Notification();
	n.put("JMSDestination","Main");
	n.put("JMSType","Test");
	send(n);

	while(!killFlag)
	    {
		try{
		    Thread.currentThread().sleep(2*1000*60);
		}
		catch (Exception e){
		};
	    }   
    }; //end construct
  
    /**
     *  sends a textual message to a node
     *
     * @param to recipient owch node name
     * @param arg the text of the message
     */

    synchronized public void receive(MetaProperties notificationIn)
    {
	Thread.currentThread().yield(); 
	if(notificationIn==null)return;
	String type;
	String subJMSType;

	type=(String)notificationIn.get("JMSType");
	subJMSType=(String)notificationIn.get("SubJMSType");
	Env.debug(8,"Client - receive type = " + type);

	if(type!=null)
	    {
		String sender;
		String room;

		if(type.equals("Test"))
		    {
			try{
			    URL p1=new URL( notificationIn.get("Path").toString());
			    URLClassLoader loader=new URLClassLoader( new URL[]{p1} );
			    loader.loadClass(notificationIn.get("Class").toString()).newInstance();
			}
			catch(Exception e)
			    {
			    };
			return;
		    }
	    };// if type != NULL
	super.receive(notificationIn);// superclass might know the JMSType

    };// end handle MetaProperties

 
 

}; // end class
