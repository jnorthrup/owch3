/*
   WebPage.java

  @author   Jim Northrup

  $Log: WebPage.java,v $
  Revision 1.3  2001/05/04 10:59:08  grrrrr
  WIP

  Revision 1.2.2.1  2001/04/30 04:27:56  grrrrr
  SocketProxy + Deploy methods

  Revision 1.2  2001/04/27 12:47:54  grrrrr
  webpages are functional, DeployAgent provides saner means of cloning.

  Revision 1.1.1.1  2001/04/26 03:06:13  grrrrr


  Revision 1.2  2001/04/25 03:35:55  grrrrr
  *** empty log message ***

  Revision 1.1.1.1  2001/04/14 20:35:26  grrrrr


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
public class WebPage extends   Node {
 
    public static void main(String[] args) { 
	Map m=Env.parseCmdLine(args);
	
	if(!(m.containsKey("JMSReplyTo")&&m.containsKey("Resource")))
	    {
		System.out.println(
				   "\n\n******************** cmdline syntax error\n"+
				   "WebPage Agent usage:\n\n"+
				   "-name name\n"+
				   "-Resource 'resource' -- the resource starting with '/' that is registered on the GateKeeper\n"+
				   "[-Clone 'host1[ ..hostn]']\n"+
				   "[-Content-Type 'application/msword']\n"+
				   "[-Clone 'host1[ ..hostn]']\n"+			
				   "[-Deploy 'host1[ ..hostn]']\n"+
				   "$Id: WebPage.java,v 1.3 2001/05/04 10:59:08 grrrrr Exp $\n"
				   );
		System.exit(2);
	    };
	
	new WebPage(m );
    };
    
    /**
       this is a set of headers that is simply nice to have... 
    */
    private Notification nice=new Notification();
    /**
       this is a set of header fields that is simply nice to have... 
    */
    private final String[]nice_headers=
    {
	"Last-Modified", 
	"Content-Type", 
	"Content-Encoding"
    };
    /** interval defines our random interval of re-registration
	starting with 1/2n..n milliseconds
    */

    byte[]payload;//for now we'll just assume payload is a static buffer
    //todo: servelet api


    /** this tells our (potentially clone) web page to stop
     * re-registering.  it will cease to spin.
     */
    public void dissolve (){ 
 	Notification n2=new Notification(); 
	n2.put("JMSDestination", "GateKeeper");
	n2.put("JMSType", "UnRegister");
	n2.put("URLSpec",get("Resource").toString());
	send(n2);
	super.dissolve();
    };
 
    /*
     *  WebPage Constructor
     *
     *  Initializes communication
     * 
     *  params:  a map -  lots o junk inside..
     */ 
    public WebPage( Map m) { 
	super(m);
	if(containsKey("Source"))
	    init((String)get("JMSReplyTo"),(String)get("Source"),(String)get("Resource"));
	else
	    init((String)get("JMSReplyTo"),(String)get("Resource"));

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
 
    public void init(String name,String file) { 
	put("JMSReplyTo",  name);

	put("Resource", file); 
	inductFile( file );
 

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
     

    public void init(String name,String url,String resource) { 
	inductURL(url);
	
    } 
 
    public void inductURL(String url){
	try{
	    URL u=new URL(url);	    
	    URLConnection uc= u.openConnection();
	    for(int i=0;i<nice_headers.length;i++){
		String hdr=uc.getHeaderField(nice_headers[i]);
		if( hdr!=null)
		    {
			nice.put(nice_headers[i],get(nice_headers[i]));
		    };
	    };
	    InputStream is=uc.getInputStream();
	    
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
	Env.getRouter("IPC").addElement(this);
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
	     
	    Env.debug(50, "-=-=-=-=-"+ getClass().getName()+":"+get("Resource").toString()+" ThreadStart " );
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
	    doIntervalJobs(); 
	    waitInterval();
	}
    }
      
    
    public void sendRegistrations()
    {
	linkTo(null);
	String resource= get("Resource").toString();
	Location l=new Location(Env.getLocation("http"));
	l.put("JMSReplyTo",getJMSReplyTo());
	Env.gethttpRegistry().registerItem(resource,l);
	Notification n2=new Notification();
	n2.put("JMSDestination", "GateKeeper");
	n2.put("JMSType", "Register");
	n2.put("URLSpec",resource   );
	n2.put("URLFwd",l.getURL() );
	send(n2);
    }

    /**
       sendPayload(socket)
       dumps our byte[] to the socket client..
       
     */
    public void sendPayload(Socket s)
    {
	/**
	   Thanks given where due...

	   HEAD /pub/GNU/guile/guile-1.4.tar.gz HTTP/1.0
	   
	   
	   HTTP/1.1 200 OK
	   Date: Sat, 14 Apr 2001 20:58:40 GMT
	   Server: Apache/1.2.1
	   Last-Modified: Tue, 20 Jun 2000 23:05:36 GMT
	   ETag: "854cf-114934-394ff8c0"
	   Content-Length: 1132852
	   Accept-Ranges: bytes
	   Connection: close
	   Content-Type: application/x-tar
	   Content-Encoding: x-gzip
	   
	   Connection closed by foreign host.*/
	
	OutputStream os   =null;
	try {
	    if (!nice.containsKey("Content-Type"))
		{
		    nice.put("Content-Type","application/octet-stream");
		};
	    if (!nice.containsKey("Content-Length"))
		{
		    nice.put("Content-Length",""+payload.length);
		}; 

	    byte[] pref=("HTTP/1.0 200 OK\n".getBytes());

	    for (int i=0;i<nice_headers.length;i++)
		{
		    if(containsKey(nice_headers[i]))
			{
			    nice.put(nice_headers[i],get(nice_headers[i]));
			};
		};
	    
	    os=new BufferedOutputStream(s.getOutputStream());
	    os.write(pref,0,pref.length);
	    nice.save(os);

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
	
	if(containsKey("Clone"))
	    {
		String clist=(String)get("Clone");
		remove("Clone");
		Env.debug(500,getClass().getName()+" **Cloning for "+clist);
		StringTokenizer st=new StringTokenizer(clist);
		while(st.hasMoreTokens())
		    clone_state1(st.nextToken());
	    };
	if(containsKey("Deploy"))
	    try {
		String clist=(String)get("Deploy");
		remove("Deploy");
		Env.debug(500,getClass().getName()+" **Cloning for "+clist);
		StringTokenizer st=new StringTokenizer(clist);
		while(st.hasMoreTokens())
		    clone_state1(st.nextToken());
		Thread.currentThread().sleep(15*1000);//kludge, allow udp messages to arrive...
		System.exit(0);//TODO: allow our host to stay alive...
	    }catch(Exception e){
		e.printStackTrace();
	    };
    }
    
    synchronized public void receive(MetaProperties n) {

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
	n2.putAll(this);
	n2.put("JMSType","DeployNode"); 
	n2.put("Class",getClass().getName());

	n2.put("JMSReplyTo",getJMSReplyTo()+"."+host);
	n2.put("Source", Env.getLocation("http").getURL()+get("Resource"));

	//resource remains constant in this incarnation
	//n2.put( "Resource",get("Resource"));//produces 3 Strings
	n2.put("JMSDestination", host);
	send(n2);
 
    }; 
};
