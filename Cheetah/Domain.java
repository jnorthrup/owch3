package Cheetah;

import owch.*; 
import java.io.*;
import java.util.*;

    /**
     * application level interface to a parent routing node.
     *
     * @author Jim Northrup
     * Modified by EDS CSTS - added userRoomList, handle code for 
     *                        WalkTo, Leave,  listClients, & GetUserRoom methods
     */

public class Domain
extends Node
{
    static int port=2112;
    static 
    {
	Room compileme;
    }; 
    /**
       contains user/room pairs*/
    public Hashtable userRoomList;


    /**
     * Handles Notifications
     *
     */


    public void handleNotification(Notification notificationIn)
    {
        String type=(String)notificationIn.get("Type");
	
	if(type!=null&&type.equals("StartRoom"))
	    {
		String msg=(String)notificationIn.get("MessageText");

		if(msg!=null)
		    {
			StringTokenizer st = new StringTokenizer(msg);
			while (st.hasMoreTokens())
			    {
				startRoom(st.nextToken());
			    };
		    };
		return;
	    } 
        super.handleNotification(notificationIn);
    }
 

    public final boolean isParent()
    {
        return true;
    }



    /**
     * default ctor
     *
     */
    public Domain()
    {
	super();
	owch.AuthenticationServer.create( );
	init();
    };


    public void init()
    {
	//Set a Terminator Bit on the infrastructure "bus"
	Env.setParentNode(true);

        userRoomList = new Hashtable();

        //Somehow we now assume getDomain will do something helpful for us.
	putAll(Env.getProtocolCache().getLocation("owch") );

	put("NodeName","default");
        Env.getNodeCache().addNode(this);

        String r=(String)get ("rooms" );
	if(r==null)
	    r="Main";

        //bootstrap test of proper configuration to be able to act as a domain
	startRoom(r);
    }

    static void main(String args[])
    {
	if(args.length==2)
	    port = Integer.getInteger(args[1]).intValue();

	new Domain ();
	try{
	    while(true){
		Thread.currentThread().sleep(60000);
	    };
	}
	catch(Exception e){
	};
    };
    /**
     * Starts a room as part of the Domain's Server
     *
     */

    public void startRoom(String name)
    {
        String [] nam=new String[1];

        if(name!=null)
	    {
		nam[0]=name;
		Env.debug(5,"debug: Domain.startRoom("+nam[0]+")");
		new Room(nam);
	    };
    };

    /**
     * returns the portnumber
     *
     */

    public int getPortNum()
    {
        return port;
    };
};


//let it not be said i dont give comments generously, here's one now :)
