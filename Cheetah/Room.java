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
    //String dir=".";
    public Map usersList; //Contains users in the room
    
   
      public static void main(String[] args) { 
	Map m=Env.parseCmdLine(args);
	
	if(!(m.containsKey("JMSReplyTo")  ))
	    {
		System.out.println(
				   "\n\n******************** cmdline syntax error\n"+
				   "Room Agent usage:\n\n"+
				   "-name name\n"+  
				   "$Id: Room.java,v 1.1 2001/04/26 03:06:12 grrrrr Exp $\n"
				   );
		System.exit(2);
	    };
	Room d=new Room(m );
	Thread t=new Thread();
	try{
	    t.start();
	    while(true)
		t.sleep(60000);
	    
	    
	}catch (Exception e){
	};
      };

    /**
     * Constructor
     *
     * @param arg currently only a single filename is accepted if anything
     *
     */ 
    public Room( Map m)
    { 
	super(m); 
	usersList=new LinkRegistry();
	Thread t=new Thread(this,"Room: "+getJMSReplyTo());
	t.start();
    };
    
    synchronized void addUser(MetaProperties clientIn)
    {
        //TODO: make MetaPropertiesects
	String userNode = clientIn.getJMSReplyTo();
	String userKey = (String) usersList.get(userNode);
	Env.debug(8,"Room - addUser = " + userNode + "key = " + userKey);
	
	if (userKey == null)
	    usersList.put(userNode,clientIn);
	
	
    };
    
    
    /**
     * This method handles the notifications based on their "JMSType"
     * property.
     *
     * @param n a MetaProperties
     *
     */
    public void receive(MetaProperties nIn)
    {
        String type=(String)nIn.get("JMSType"); 
        Env.debug(8,"Room - receive type = " + type);  
        String name=nIn.getJMSReplyTo();
	
	if ( type!=null&&type.equals("Test")){
	      
	    MetaProperties  n=  new Notification(Env.getLocation("http"));
	    n.put("JMSDestination", nIn .get("JMSReplyTo"));
	    n.put("JMSType","Test"); 
	    String url= n.get("URL")+"/test.jar";
	    n.put("Path",url);
	    n.put("Class","Cheetah.TestWindow");
	    n.put("args","");
	    send(n);
	    return;
	};
	super.receive(nIn);
    }
    /** 
     * publishs MetaPropertiess to all users in the room
     */

    public void publish(MetaProperties nIn)
    {

	String tmpJMSReplyTo; 
	String roomServerName=getJMSReplyTo();
	nIn.put("ResentFrom",roomServerName);
	nIn.remove("JMSMessageID");

	//for each user in room
	for (Iterator e = usersList.keySet().iterator(); e.hasNext() ;)
	    {
		tmpJMSReplyTo=(String)e.next();
		nIn.put("JMSDestination",tmpJMSReplyTo);
		Env.debug(8,"Room.Publish ("+roomServerName+")("+
			  nIn.getJMSReplyTo()+
			  ") user is "+nIn.get("JMSDestination"));
		send(new Notification (nIn));
	    }; // end for

    } // end publish method

    /**
     * Updates the status of a user, and the user's view of users in the same room.
     *
     * @param the notification to publish
     */

    synchronized public void userUpdate(MetaProperties clientIn)
    {

        addUser(clientIn);
	
	syncUsers(clientIn.get("JMSReplyTo").toString());
    };

    synchronized public void syncUsers(String nodeNameIn)
    {
	String tmpJMSReplyTo = null;

	MetaProperties notificationOut = new Notification();

	Env.debug(8,"Room - syncUsers  ");

	notificationOut.put("JMSType","UserUpdate");
	notificationOut.put("SubJMSType","Add");
	notificationOut.put("JMSDestination",nodeNameIn);
	String roomServerName=getJMSReplyTo();
	notificationOut.put("ResentFrom",roomServerName);

 	//for each user in room
	for (Iterator e = usersList.keySet().iterator(); e.hasNext() ;)
	    {
		tmpJMSReplyTo=(String)e.next();
		notificationOut.put("JMSReplyTo",tmpJMSReplyTo);
		Env.debug(8,"Room.Publish ("+roomServerName+")("+ notificationOut.getJMSReplyTo()+ ") user is"+notificationOut.get("JMSDestination"));
		send(new Notification (notificationOut));
	    }; // end for

    } //end synUsers


    /**
     * typical threadspawn code.
     *
     */
    public void run()
    { 
        linkTo(Env.getParentNode().getJMSReplyTo());
        

        StringBuffer s;

        while(true)//todo: watch runstate
	    { 
		wait120();

		// commented out - causes relink - much network traffic
		linkTo(Env.getParentNode().getJMSReplyTo());
		//linkTo(Env.getProxyCache().enumProxies());
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
