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

public class Domain extends Node implements javax.naming.ldap.LdapContext
{
   
      public static void main(String[] args) { 
	Map m=Env.parseCmdLine(args);
	
	if(!(m.containsKey("JMSReplyTo")&&m.containsKey("HostPort") ))
	    {
		System.out.println(
				   "\n\n******************** cmdline syntax error\n"+
				   "Domain Agent usage:\n\n"+
				   "-name name\n"+ 
				   "-HostPort port\n"+ 
 				   "$Id: Domain.java,v 1.3 2001/05/04 10:59:08 grrrrr Exp $\n"
 
				   );
		System.exit(2);
	    };
	Env.setParentHost(true);
	Domain d=new Domain( m ); 
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
    public Domain(Map p)
    {
	super(p);  
	Env.getLocation("owch");
    };




    /**
     * Starts a room as part of the Domain's Server
     *
     */

    public void startRoom(String name)
    { 

        if(name!=null)
	    { 
		Env.debug(5,"debug: Domain.startRoom("+name+")");
		Location l=new Location();
		l.put("JMSReplyTo",name);
		new Room(l);
	    };
    };

  
};


//let it not be said i dont give comments generously, here's one now :)
