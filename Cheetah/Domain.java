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
 
    /**
       args 1=port
       args 2=iface
     */
    static void main(String args[])
    {
	int port=2112; 
	String host="localhost";

	
	System.out.println(args);
	if(args.length>1)
	    port=Integer.valueOf(args[1]).intValue(); 

 	ListenerCache lc;
	lc=new ListenerCache();
	if(args.length>0)
	    Env.setHostname(args[0]);

	lc=new ListenerCache();
	lc.put(Env.getDatagramFactory().create(port,8 ));
	Env.getProtocolCache().put("owch",lc);
	lc=new ListenerCache();
	lc.put(Env.getHTTPFactory().create(port,8 ));
	Env.getProtocolCache().put("http",lc);
    
	
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
       contains user/room pairs*/
    public Hashtable userRoomList;


    /**
     * Handles MetaPropertiess
     *
     */


    public void receive(MetaProperties notificationIn)
    {
        String type=(String)notificationIn.get("JMSType");
	
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
        super.receive(notificationIn);
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
	init();
    };


    public void init()
    {
	//Set a Terminator Bit on the infrastructure "bus"
	Env.setParentNode(true);

        userRoomList = new Hashtable();

        //Somehow we now assume getDomain will do something helpful for us.
	putAll(Env.getProtocolCache().getLocation("owch") );

	put("JMSReplyTo","default");
        Env.getNodeCache().addNode(this);
 
    }


    /**
     * Starts a room as part of the Domain's Server
     *
     */

    public void startRoom(String name)
    { 

        if(name!=null)
	    { 
		Env.debug(5,"debug: Domain.startRoom("+name+")");
		new Room(name);
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
