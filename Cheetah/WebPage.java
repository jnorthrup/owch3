/*
  Deploy.java

  @author   Jim Northrup

  $Log: WebPage.java,v $
  Revision 1.1  2001/04/14 20:35:26  grrrrr
  Initial revision

  Revision 1.2  2001/04/12 19:09:50  grrrrr
  *** empty log message ***

*/


package Cheetah;

import owch.*;
import java.io.*;
import java.net.*;
import java.util.*;


/**
   base class proof of concept for an object that can start up, suck
   down some content, clone, and finally take over functions. this
   class carries content far away safe from harm.
   
 */
public class WebPage extends   Node implements Runnable{
    
    /** interval defines our random interval of re-registration
	starting with 1/2n..n milliseconds
    */
    private long interval=60*1000 * 2; 
    private boolean killFlag=false;
    byte[]payload;//for now we'll just assume payload is a static buffer
    //todo: servelet api

    /** this tells our (potentially clone) web page to stop
     * re-registering.  it will cease to spin.
     */
    public void dissolve (){
	killFlag=true;
	Notification	n2=new Notification(); 
	n2.put("JMSDestination", "GateKeeper");
	n2.put("JMSType", "UnRegister");
	n2.put("URLSpec",get("Resource").toString());
	send(n2);
	Env.getHTTPRegistry().unregisterURLSpec(get("Resource").toString());
	Env.getNodeCache().remove(getJMSReplyTo());
	//unlink(null)  commented out here because we don't want to unlink a clone

    };
    public static void main(String[] args) { 
	Map m=Env.parseCmdLine(args);
	
	if(!(m.containsKey("JMSReplyTo")&&m.containsKey("Resource")))
	    {
		System.out.println(
				   "\n\n******************** cmdline syntax error\n"+
				   "WebPage Agent usage:\n\n"+
				   "-name name -Resource 'resource'\n\n"+
				   "$Id: WebPage.java,v 1.1 2001/04/14 20:35:26 grrrrr Exp $\n"
				   );
		System.exit(2);
	    };
	new WebPage((String)m.get("JMSReplyTo"),(String)m.get("Resource"));
    };

 

    /*
     *  WebPage Constructor
     *
     *  Initializes communication
     * 
     *  params: name -- we name our agent anything we want..
     *           
     *  url -- name of a file
    */  
    public WebPage(String name,String file) { 
	put("JMSReplyTo",  name);
	put("Resource", file);
	inductFile( file );
	Env.getNodeCache().addNode(this); 
	new Thread(this).start();  
    }
    /*
     *  WebPage Constructor
     *
     *  Initializes communication
     * 
     *  params: name -- we name our agent anything we want..
     *           
     *  url -- name of a file
    */  
    public WebPage(String name,String url,String resource) { 
	put("JMSReplyTo",  name);
	put("Resource", resource);
	inductURL(url);
	Env.getNodeCache().addNode(this);    
	new Thread(this).start();  
    }
        
        
    public void inductURL(String url){
	try{
	    URL u=new URL(url);	    
	    InputStream is = u.openStream();
	    inductStream(is); 	     
	}catch(Exception e)
	    {
		e.printStackTrace();
	    };
    };
    
    public void inductFile(String file){
	try{
	    String  resource=file;
	    if( resource.startsWith("/"))
		resource= resource.substring(1);
	    //the assumption is being made that our current work directory
	    //wont change...
	    put("Resource",file);

	    if( resource.startsWith("/"))
		resource= resource.substring(1);
	    InputStream   is=new FileInputStream( resource ); 
	    inductStream(is);	
	}
	catch(Exception e)
	    {
		e.printStackTrace();
	    };
    } 
    public void inductStream(InputStream is){
	try{ 
	    ByteArrayOutputStream os=new ByteArrayOutputStream();
	    byte buf[]=new byte[16384];
	    int actual=0;
	    int avail=0;
	    while(actual!=-1){
		avail=is.available();
		actual=is.read(buf);
		if(actual>=0)
		  {  os.write(buf,0,actual);
		  Env.debug(50,  getClass().getName()+":"+get("Resource").toString()+" slurped up "+actual+" bytes" );
		  } 
	
	    }
	    payload=os.toByteArray();
	    os.flush();
	    os.close();

	}catch (Exception e)
	    {
		Env.debug(50, getClass().getName()+":"+get("Resource").toString()+" failure "+e.getMessage());
		e.printStackTrace();
		return;
	    }
	
    }
    
    
    
    /**
       sets the IM HERE interval for our periodic re-registration tasks.
    */

    public void setInterval(long ival)
    {
	interval=ival;
    }
    /**
       our thread function is to periodically wake up and notify both
       the Domain and the Gatekeeper of our existance every so often.
       this allows changing of the guard and a roundabout load
       balancing of competing WebPage clones sharing a single
       gatekeeper

    */
    public void run(){
	while(!killFlag){
	    sendRegistrations();
	    pause();
	};
    } 
    
    public void sendRegistrations()
    {
	linkTo(null);
	String resource= get("Resource").toString();
	Location l=new Location(Env.getLocation("http"));
	l.put("JMSReplyTo",getJMSReplyTo());

	Env.getHTTPRegistry().registerURLSpec(resource,l);
	Notification n2=new Notification();
	n2.put("JMSDestination", "GateKeeper");
	n2.put("JMSType", "Register");
	n2.put("URLSpec",resource   );
	n2.put("URLFwd",l.getURL() );
	send(n2);
    }
    public void pause()
    { 
	try{ 	
	    long tim=(long)(Math.random()*(interval/2.0)  +(interval/2.0));
	    Env.debug(12, getClass().getName()+" waiting for "+tim+" ms.");
	    Thread.currentThread().sleep(tim);
	}
	catch(Exception e){
	}
    }
    
    /**
       sendPayload(socket)
       dumps our byte[] to the socket client..
       
     */
    public void sendPayload(Socket s)
    {
	OutputStream os   =null;
	try {
	    String type=(String)get("Content-Type");
	    if(type==null)
		type="application/octet-stream";
	    byte[] pref=("HTTP/1.0 200 OK\nContent-Type: "+type+"\n\n").getBytes();
	    
	    os=new BufferedOutputStream(s.getOutputStream());
	    os.write(pref,0,pref.length);
	    
	    byte buf[]=new byte[16384];
	    int actual=0;
	    int avail=0;
	    InputStream is=new ByteArrayInputStream(payload);
	    do{
		avail=is.available();
		if (avail>=0)
		    {
			actual=is.read(buf);
			if(actual>0){
			    Env.debug(50, getClass().getName()+":"+getJMSReplyTo()+" sent "+actual+" bytes" );
			    os.write(buf,0,actual);
			};
		    }; 
	    }while(actual!=-1);
	 
	   
	    os.flush();
	    os.close();
	    s.close();
	}catch(Exception e)
	    {
		Env.debug(15, getClass().getName()+":"+getJMSReplyTo()+" failure "+e.getMessage());
		e.printStackTrace();
	    } 
    };
    
    /**
       intended for automatic maintenance.. so to speak, for survival
       tasks.. period replication, metrics evalutation, etc.  lots o
       TBD in this method
     */
    public void doIntervalJobs()
    {
	//INHERIT ME
    }
    
    synchronized public void receive(MetaProperties n) {
        Thread.currentThread().yield();
        if (n == null)
	    return;
        String type;
        String subJMSType;
        type = (String)n.get("JMSType"); 
        Env.debug(8, getClass().getName()+" receive type = " + type);
        if (type != null) {
            String sender;
	    /**
	       
	    incoming Message type Move
	    
	    Host - name of a Deploy agent
	    */

	    Notification n2;      

	    if (type.equals("httpd")) {
		Socket s=(Socket)n.get("_Socket");
		sendPayload(s);
		return;
	    }else
		if (type.equals("Clone")) {
		    /*
		      clone - recv order to clone, and host.  deploy
		      new class.  .  deliver content.  close channel.
		     */
		String host=n.get("Host").toString(); //name of a Deploy agent
		if(host==null)
		    host=Env.getHostname();
		clone_state1(host);
	    }else
		if (type.equals("Move")) {
		    /*    move - clone self. node arrives at new host,
		     *    registers in Nodecache on opaque name. sends
		     *    "Dissolve" message and registers original
		     *    name.  Dissolver registers new opaque name,
		     *    informs new clone to register. awaits
		     *    dissolve.
		     */
		    String host=n.get("Host").toString(); //name of a Deploy agent
		    if(host==null)
			host=Env.getHostname();
		    // move_state1(host);
		}else
		    if (type.equals("Dissolve"))
			{
			    dissolve();
			    return;
			} 
          
        }; // if type != NULL
        super.receive(n); // superclass might know the JMSType
    }; // end handle MetaProperties 
 
    public void clone_state1(String host)
    { 
	 MetaProperties n2=Env.getLocation("http");  
	n2.put("JMSType","Deploy"); 
	n2.put("Class",getClass().getName());
	n2.put("Signature","java.lang.String java.lang.String java.lang.String"); 
	n2.put("Parameters",getJMSReplyTo()+" "+Env.getLocation("http").getURL()+get("Resource")+" "+get("Resource"));//produces 3 Strings
	n2.put("JMSDestination", host);
	send(n2);
    }; 
};
