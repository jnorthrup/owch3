/*
  Deploy.java

  @author   Jim Northrup

  $Log: Deploy.java,v $
  Revision 1.1  2001/04/12 19:07:26  grrrrr
  Initial revision

*/


package Cheetah;

import owch.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Deploy extends   Node {
    
    public static void main(String[] args) {
        String host = "localhost";
        int port = 2112;
        String JMSReplyTo = "host_"+Env.getHostname(); //one per host should be adequate..

	System.out.println(args);
        if (args.length > 2)
            host = args[2];
        if (args.length > 1)
            port = Integer.valueOf(args[1]).intValue();
        if (args.length > 0)
            JMSReplyTo = args[0];
        new Deploy(JMSReplyTo);
    };

    /*
     *  Client Constructor
     *
     *  Initializes communication
     */

    public Deploy(String name) {
	put("JMSReplyTo", name);
        Env.getNodeCache().addNode(this);
        linkTo("default");

    } 
    public Deploy( ) {
	put("JMSReplyTo",  "host_"+Env.getHostname()  );
        Env.getNodeCache().addNode(this);
        linkTo("default"); 
    }
    synchronized public void receive(MetaProperties n) {
        Thread.currentThread().yield();
        if (n == null) return;
        String type;
        String subJMSType;
        type = (String)n.get("JMSType"); 
	
	/**
	 *  Message: Deploy
	 * 
	 *  Fields:
	 *   Class - class to be constructed
	 *   Path  - Array of URL Strings for Classpath, or "default" for native classloader
	 *   Parameters  -  array of normalized Strings to pass into our new object
	 *   
 	 */
	
        Env.debug(8, "Deploy - receive type = " + type);
        if (type != null) {
            String sender;
            String room;
            if (type.equals("Deploy")) {
                try {
		    
		    
		    String _class=n.get("Class").toString();
		    String path=n.get("Path").toString();
		    String parm=n.get("Parameters").toString();
		    String signature=n.get("Signature").toString();
		    
		    StringTokenizer st;
		    List l;
		    
		    st=	new StringTokenizer(parm);
		    l=new ArrayList();
		    while (st.hasMoreTokens()) {
			l.add(new URL(st.nextToken()));
		    };
		    List parms=l;
		    Class c;
		    if (!path.equals("default")){
			st= new StringTokenizer(path);
			l=new ArrayList();
			while (st.hasMoreTokens()) {
			    l.add(new URL(st.nextToken()));
			};
			URL[]ul=new URL[l.size()]; 
			System.arraycopy(l.toArray(), 0, ul, 0,ul.length); 
			URL p1 = new URL(n.get("Path").toString());
			URLClassLoader loader = new URLClassLoader( ul );
			c=loader.loadClass(_class);
		    }
		    else//default path case -- simple Cheetah  agents prolly
			{
			    c=Class.forName(_class);
			}
		    if(parm.equals(""))
			{
			    c.newInstance();
			}else
			    {
				Class []sig;
				if (signature==null)
				    sig=new Class[]{
					Object[].class
				    };
				else{
				    //parse Signature
				    
				    st= new StringTokenizer(signature);
				    l=new ArrayList();

				    while (st.hasMoreTokens()) {
					l.add(Class.forName(st.nextToken()));
				    };
				    sig=new Class[l.size()]; 
				    System.arraycopy(l.toArray(), 0, sig, 0,sig.length);   
				};
				c.getConstructor(sig).newInstance(parms.toArray());
			    }
                }
	    catch (Exception e) {
		Env.debug(10,getClass().getName()+" recv failure "+e.getMessage());
		    
	    };
		return;
	    }
	}; // if type != NULL
	super.receive(n); // superclass might know the JMSType
    }; // end handle MetaProperties 
};
