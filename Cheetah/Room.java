/*
 * Room.java
 *************************************************************************************

  Created by Jim Northrup

  Modified by   EDS CSTS
  @version     2.0 12/15/96

  Backend implemenation of Room.


*************************************************************************************/
package Cheetah;

import owch.*; 
import java.util.*;
import java.io.*; 

public class Room extends Node implements Runnable
{
    static {
	Client compileMe;
    }
    
    static void main(String args[])
    {
	if (args.length!=2)
	    System.out.println("Usage: java Cheetah.Room <room name> <domain URL 'owch://hostname:port>'");
	throw new Error("usage error");
	
    }
    //String dir=".";
    public HashMap usersList; //Contains users in the room
    
    /**
     * Constructor
     *
     * @param arg currently only a single filename is accepted if anything
     *
     */ 
    public Room(String[] arg)
    {
        if(arg.length>0)
	    put("NodeName",arg[0]);
	
	usersList=new LinkRegistry();
	
	Thread t=new Thread(this,"Room: "+getNodeName());
	t.start();
    };
    
    synchronized void addUser(Notification clientIn)
    {
        //TODO: make Notificationects
	String userNode = clientIn.getNodeName();
	String userKey = (String) usersList.get(userNode);
	Env.debug(8,"Room - addUser = " + userNode + "key = " + userKey);
	
	if (userKey == null)
	    usersList.put(userNode,clientIn);
	
	
    };
    
    
    /**
     * This method handles the notifications based on their "Type"
     * property.
     *
     * @param n a Notification
     *
     */
    synchronized public void handleNotification(Notification notificationIn)
    {
        String type=(String)notificationIn.get("Type");
        String subType=(String)notificationIn.get("SubType");
        Env.debug(8,"Room - handleNotification type = " + type);  
        String name=notificationIn.getNodeName();
	if ( type!=null&&type.equals("Test")){
	    Notification  n=  new Notification(Env.getLocation("http"));
	    n.put("DestNode", notificationIn .get("NodeName"));
	    n.put("Type","Test"); 
	    String url= n.get("URL")+"/test.jar";
	    n.put("Path",url);
	    n.put("Class","Cheetah.TestWindow");
	    n.put("args","");
	    route(n);
	    return;
	};
	super.handleNotification(notificationIn);
    }
    /** 
     * broadcasts Notifications to all users in the room
     */

    synchronized public void broadcast(Notification notificationIn)
    {

	String tmpNodeName; 
	String roomServerName=getNodeName();
	notificationIn.put("ResentFrom",roomServerName);
	notificationIn.remove("SerialNo");

	//for each user in room
	for (Iterator e = usersList.keySet().iterator(); e.hasNext() ;)
	    {
		tmpNodeName=(String)e.next();
		notificationIn.put("DestNode",tmpNodeName);
		Env.debug(8,"Room.Broadcast ("+roomServerName+")("+
			  notificationIn.getNodeName()+
			  ") user is "+notificationIn.get("DestNode"));
		route(new Notification (notificationIn));
	    }; // end for

    } // end broadcast method

    /**
     * Updates the status of a user, and the user's view of users in the same room.
     *
     * @param the notification to broadcast
     */

    synchronized public void userUpdate(Notification clientIn)
    {

        addUser(clientIn);
	
	syncUsers(clientIn.get("NodeName"));
    };

    synchronized public void syncUsers(String nodeNameIn)
    {
	String tmpNodeName = null;

	Notification notificationOut = new Notification();

	Env.debug(8,"Room - syncUsers  ");

	notificationOut.put("Type","UserUpdate");
	notificationOut.put("SubType","Add");
	notificationOut.put("DestNode",nodeNameIn);
	String roomServerName=getNodeName();
	notificationOut.put("ResentFrom",roomServerName);

 	//for each user in room
	for (Iterator e = usersList.keySet().iterator(); e.hasNext() ;)
	    {
		tmpNodeName=(String)e.next();
		notificationOut.put("NodeName",tmpNodeName);
		Env.debug(8,"Room.Broadcast ("+roomServerName+")("+ notificationOut.getNodeName()+ ") user is"+notificationOut.get("DestNode"));
		route(new Notification (notificationOut));
	    }; // end for

    } //end synUsers


    /**
     * typical threadspawn code.
     *
     */
    public void run()
    {
        Env.getNodeCache().addNode(this);
        linkTo(Env.getParentNode().getNodeName());
        linkTo(get("links"));

        StringBuffer s;

        while(true)//todo: watch runstate
	    { 
		wait120();

		// commented out - causes relink - much network traffic
		linkTo(Env.getParentNode().getNodeName());
		linkTo(Env.getProxyCache().enumProxies());
	    };
    };
    /** 
     * waits an interval averageing 120 seconds.
     *
     */

    synchronized final private static void wait120()
    {

        //Random
        try
	    {
		long tim=(long)(Math.random()*180.0*1000.0)+60000;
		Env.debug(12,"debug: wait120 Waiting for "+tim+" ms.");
		Thread.currentThread().sleep(tim,0);
	    }catch(Exception e)
		{

		}
        Env.debug(13,"debug: wait120 end");
    }

};
