package owch;

import java.io.*;
import java.util.*;

   /**
    * RNODI Serializable Properties class.
    * java.util.Properties.load() requires EOF, the intent of MetaProperties is to keep
    * the Properties Interface and insert indirection to circumvent closing streams to receive input.
    * (early tests proved impossible to stream multiple Properties classes on the same stream.)
    * This enables a socket connection stream caching mechanism and allows us to gain state info clues
    * on remote nodes via watching tcp exceptions popping.  We thus avoid using udp to write our own
    * reliable connection state while keeping the fairly expedient response of an open socket.
    *
    * @version 0.5 22 Aug 96
    * @author Jim Northrup
    */
public abstract class MetaProperties extends HashMap implements MetaNode
{
    static {
	Env.registerFormat(getDefaultFormat(),
			   new RFC822Format());
	
    };
    
    
 
    final int key=0;
    final int token=1;
    final int data=2;
    final int comment=3;
    final int newline=4;
    final int empty=5;
    final int statelen=6;

    /**
     * RNODI specific Properties Serialization input.
     *
     * @param istream Source of input.
     * @exception java.io.IOException thrown if istream throws an Exception.
     */

    public final void load(InputStream istream)
	throws IOException
    {
        Env.getFormat(  getDefaultFormat()).read(istream,this);
    }

    public final String getURL()
    {
        String s=(String)get("URL");
        return s;
    }

    /**
     * Save properties to an OutputStream.
     */
    public synchronized void save(OutputStream os) throws IOException
    { 
	Env.getFormat( getDefaultFormat()).write(os,this);
    }
    public static  String getDefaultFormat(){
	return "RFC822";
    };

    //String

    /**
     * Copy ctor.
     * MetaProperties really don't make too much use of default Properties.
     * This flattens the class so that access to Properties.defaults is unneccesary at save time.
     *
     * @param p Source of copy.
     */

    public MetaProperties(Map p)
    {
        //HashMap
        super(12,(float)0.75);
	putAll(p);
    };

    /**
     * Default ctor.
     */

    public MetaProperties()
    {         //Hashtable
        super(12,(float)0.75);
    };
    /**
     * Default ctor.
     */

    public final String getNodeName()
    {
        return (String)get("NodeName");
    };
    public final String get(String key)
    {
        return (String) super.get(key);
    };
    public final String put(String key,String value)
    {
        return (String)super.put(key,value);
    };
};

