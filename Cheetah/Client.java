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

public class Client extends Node implements Runnable
{
    static String host="localhost";
    static    int port=2112;
    static   String JMSReplyTo="Client";

	

    public static  void main(String args[]){
	 
	System.out.println(args);
	if(args.length>2)
	    host=args[2];
	if(args.length>1)
	    port=Integer.valueOf(args[1]).intValue(); 
	if(args.length>0)
	    JMSReplyTo=args[0];
 

	new Client();
    };
    
    /*
     *  Client Constructor
     *
     *  Initializes communication 
     */
	
    public Client( )
    {  
	
	
	put("JMSReplyTo",JMSReplyTo); 
	linkTo("Main");

	MetaProperties n=new Notification();
	n.put("JMSDestination","Main");
	n.put("JMSType","Test");
	send(n);

	while(true)
	    {
		try{
		    Thread.currentThread().sleep(200000);
		}
		catch (Exception e){};
	    }   
    }; //end construct

    /** test clients don't need threads
     */
    public void run(){
    }
 

 

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
