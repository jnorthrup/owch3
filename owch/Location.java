package owch;

import java.net.*;
import java.io.*;
import java.util.*;

    /**
	 * Provides information of how to reference a Node out on the network,
     * contains the most recently known network address of a listener,
     * and it's clones.
     * @version 0.5 22 aug 96
     * @author 	Jim Northrup
     */

public class Location extends MetaProperties
{

 

    /**
     * Inserts "URL" property from a given ServerSocket.
     *
     * @TODO full dns hostnames.
     * @param serverSocket Reference to be a Location to.
     */

    public static Location create(ListenerReference lr)
    {
        String tstring=new String(lr.getProtocol()+":");
	Location l=new Location();
        try
	    {

		String myIP=InetAddress.getLocalHost().toString();
		myIP=myIP.substring(myIP.indexOf('/')+1);
		tstring+="//"+myIP.trim()+":"+lr.getServer().getLocalPort();
	    }catch(UnknownHostException e)
		{
		    tstring+="broked";
		};
        l.put("URL",tstring);

	return l;
    }

    /**
     * Default Ctor
     *
     *
     */
    public Location()
    { 
    };

    /**
     * Copy Constructor
     *
     * @param p  source of copy
     */
    public Location(Map p)
    {
        super(p);
    }
    ;
};

