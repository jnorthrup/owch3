package owch;

import java.net.*;
import java.io.*;
import java.util.*;

    /**
	 * Provides information of how to reference a Node out on the network,
     * contains the most recently known network address of a listener,
     * and it's clones.
     * @version $Id: Location.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
     * @author James Northrup*/

public class Location extends TreeMap implements MetaProperties 
{ 
  static 
    {	
	try{
	    Env.registerFormat("XMLSerial",(Format)Class.forName("msg.format.XMLSerialFormat").newInstance());
	}catch(Exception e){
	    Env.debug(5,"XML Format not loaded");
	}     
	Env.registerFormat( "RFC822",
			    new RFC822Format());
	try{
	    Env.registerFormat("Serial",(Format)Class.forName("msg.format.SerialFormat").newInstance());
	}catch(Exception e){
	    Env.debug(5,"Serial Format not loaded");
	}
    };
    private String format="RFC822";
    
    /**
     * Inserts "URL" property from a given ServerSocket.
     *
     * @TODO full dns hostnames.
     * @param serverSocket Reference to be a Location to.
     */

    public static  Location create(ListenerReference lr)
    {
	
        String tstring=new String(lr.getProtocol()+":");
	Location l=new Location();
        
	tstring+="//"+Env.getHostname().trim()+":"+((Env.getHostPort()==0)?lr.getServer().getLocalPort():Env.getHostPort());
		
        l.put("URL",tstring);
	
	return l;
    }
    
    /**
     * RNODI specific Properties Serialization input.
     *
     * @param istream Source of input.
     * @exception java.io.IOException thrown if istream throws an Exception.
     */
    public final void load(InputStream istream)
	throws IOException
    {
        Env.getFormat(  getFormat()).read(istream,this);
    }

    /**
     * Save properties to an OutputStream.
     */
    public synchronized void save(OutputStream os) throws IOException
    { 
	Env.getFormat( getFormat()).write(os,this);
    }
    
    public void setFormat(String format){
	this.format=format;
    }

    public   String getFormat(){
	return format;
    };

    public final String getURL()
    {
        String s=(String)get("URL");
        return s;
    }

    /**
     * Default ctor.
     */
    public final String getJMSReplyTo()
    {
        return (String)get("JMSReplyTo");
    };

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
        putAll(p);
    }    ;
};

