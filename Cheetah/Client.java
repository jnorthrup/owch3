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
    public static  void main(String args[]){
	String host="localhost";
	int port=2112;
	String NodeName="Client";
	
	System.out.println(args);
	if(args.length>0)
	    host=args[0];
	if(args.length>1)
	    port=Integer.valueOf(args[1]).intValue(); 
	if(args.length>2)
	    NodeName=args[2];


	Location l=new Location(Env.getLocation("owch")); 
	try{
	    Socket s=new Socket(host,port);
	    l.put("NodeName",NodeName);
	    l.save(s.getOutputStream());
	    l.load(s.getInputStream());
	    s.close(); 
	}
	catch(Exception e){
	    System.err.println(e);
	}

	Env.debug(100,l.toString());
	Env.debug(100,Env.getLocation("http").toString());
	new Client(l);
    };
    
    /*
     *  Client Constructor
     *
     *  Initializes communication 
     */
	
    public Client(MetaProperties MyInfo)
    {  
	super(MyInfo);
	Location clientLocation =new Location();
	clientLocation.put("URL",MyInfo.get("ParentURL"));
	clientLocation.put("NodeName","default");
	clientLocation.put("Type","UserUpdate");
	clientLocation.put("DestNode",MyInfo.getNodeName());
  
	Env.getNodeCache().addNode(this);
	Env.getProxyCache().addQueue(new Notification(clientLocation));
  
	Env.debug(19,"debug: Client kicking off the MainWindow");
	System.out.println( toString());

	linkTo("Main");
	Notification n=new Notification();
	n.put("DestNode","Main");
	n.put("Type","Test");
	route(n);

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
    public void unlink()
    {
	Env.getNodeCache().remove(getNodeName());
    };

 

    /**
     *  sends a textual message to a node
     *
     * @param to recipient owch node name
     * @param arg the text of the message
     */

    synchronized public void handleNotification(Notification notificationIn)
    {
	Thread.currentThread().yield(); 
	if(notificationIn==null)return;
	String type;
	String subType;

	type=(String)notificationIn.get("Type");
	subType=(String)notificationIn.get("SubType");
	Env.debug(8,"Client - handleNotification type = " + type);

	if(type!=null)
	    {
		String sender;
		String room;

		if(type.equals("Test"))
		    {
			try{
			    URL p1=new URL( notificationIn.get("Path"));
			    URLClassLoader loader=new URLClassLoader( new URL[]{p1} );
			    loader.loadClass(notificationIn.get("Class")).newInstance();
			}
			catch(Exception e)
			    {
			    };
			return;
		    }
		 
	 
	    };// if type != NULL
	super.handleNotification(notificationIn);// superclass might know the Type

    };// end handle Notification

 
 

}; // end class
