/*
  Deploy.java

  @author   Jim Northrup

  $Log: Deploy.java,v $
  Revision 1.1  2001/04/14 20:35:26  grrrrr
  Initial revision

  Revision 1.1.1.1  2001/04/12 19:07:26  grrrrr


*/


package Cheetah;

import owch.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Deploy extends Node {
    
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
	
	while(true){
	    try{
		//we go snooze now..
		Thread.currentThread().wait();
	    }catch(Exception e){}
	}

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
             
		    String _class=(String)n.get("Class");        Env.debug(500,"Deplying::Class "+_class);
		    String path=(String)n.get("Path");	         Env.debug(500,"Deplying::Path "+ path);
		    String parm=(String)n.get("Parameters");     Env.debug(500,"Deplying::Parameters "+  parm);
		    String signature=(String)n.get("Signature"); Env.debug(500,"Deplying::Signature "+   signature);
		    int i;
		    List tokens;
		  
		    //we use a classloader based on our reletive origin..
		    ClassLoader loader=getClass().getClassLoader();
		    

		    String[]tok_arr=new String[]{
			path,parm,signature
		    };
		    Object[][] res_arr=new Object[tok_arr.length][];
		    
		    StringTokenizer st;
		    for(  i=0;i<tok_arr.length;i++)
			{
			    String temp_str=tok_arr[i];
			    tokens=new ArrayList();
			    if(temp_str==null ){
				Env.debug(500,"Deploy tokenizing nullinating  "+i);
				temp_str="";
			    }
			    st=new StringTokenizer(temp_str);
				
			    while(st.hasMoreElements())
				tokens.add(st.nextElement());

			    res_arr[i]=tokens.toArray();
			    Env.debug(500,"Deploy arr"+i+" found "+ res_arr[i].length+" tokens");
			}
			
		    URL   [] path_arr= new URL[res_arr[0].length];
		    Object[] parm_arr= new Object[res_arr[1].length];
		    Class [] sig_arr = new Class[res_arr[2].length]; 
		    
		     try { 
		    //path is URL's, gotta do a loop to instantiate URL's... 
		    for (i=0;i<res_arr[0].length;i++) 
			   path_arr[i]=new URL((String)res_arr[0][i]);
		 

		    //parms are strings, easy copy there...
		    System.arraycopy(res_arr[1],0, parm_arr,0,res_arr[1].length);
		    
		    //determine if our loader is based on URLS....
		    if( path_arr.length!=0)//if we have URL's we need to make a new loader that adds these URLS to our path
			loader=new URLClassLoader(path_arr,loader);
		    
		    //user our loader to populate sig_arr with a Class[]
		    for (i=0;i<res_arr[2].length;i++) 
			    sig_arr[i]=loader.loadClass((String)res_arr[2][i]);
		 
		    
		    /*this creates a new Object*/
		    loader.loadClass(_class).getConstructor(sig_arr).newInstance(parm_arr);
	 
		}
		catch(Exception e)
		    {
			e.printStackTrace();
		    }
	    }; // if type != NULL
	    super.receive(n); // superclass might know the JMSType
	}; // end handle MetaProperties 
    };
}
