package owch;

import java.net.*;
import java.util.*;
import java.io.*;
/**
 *  Http server daemon used for sending files and routing agent notifications.
 */
public class HTTPServer extends TCPServerWrapper implements ListenerReference, Runnable
{
    int threads;

    public String getProtocol(){
	return "http";
    };
    public long getExpiration() {
	return (long) 0;
    }
    public int getThreads(){
	return threads;
    }
    public ServerWrapper getServer(){
	return this;
    };

    public void expire(){	
	getServer().close();
    };

    public HTTPServer(int port,int threads)throws java.io.IOException{
	super(port);
	this.threads=threads;
	try{
	   for(int i=0;i<threads;i++)
	       new Thread(this).start(); 
	}
	catch(Exception e){ Env.debug(2,"HTTPServer creation Failure");
	};
    }; 

    /**called only on a new socket
     */   
    public MetaProperties getRequest(Socket s ){
	String line="";
        Env.debug(100,"HTTPServer.getRequest");
	Notification n=new Notification();
	try{	   
	    n.setFormat("RFC822");
	    DataInputStream ins
		= new DataInputStream( s.getInputStream() ); 
	    line=ins.readLine();
	    n.load(ins);
	    n.put("Request",line); 
	}catch(java.lang.Exception e){
	    Env.debug(5, "had a DynServer Snag, retry");
	}
	Env.debug(50, "returning "+n.toString());
	return n;
    }

    /**default action of an agent host is to just send a file.
     */
    public void  sendFile(Socket s,String file){


	/** errors would send...
HTTP/1.1 404 Not Found
Date: Sun, 08 Apr 2001 21:31:24 GMT
Server: Apache/1.3.12 (Unix) mod_perl/1.24
Connection: close
Content-Type: text/html; charset=iso-8859-1



	 */
 
	try{
	    boolean found=true;
	    byte[] pref=null; 
	    if(file.startsWith("/"))
		file=file.substring(1);
	    FileInputStream is=null;
	    try{
		is=new FileInputStream(file);
		
	    }catch (Exception e)
		{
		    found=false;
		    pref=
			new String("HTTP/1.0 404 "+e.getMessage()+"\nConnection: close\n\n<!DOCTYPE HTML PUBLIC -//IETF//DTD HTML 2.0//EN><HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY><H1>"+e.getMessage()+"</H1>The requested URL "+file+" was not found on this server.<P></BODY></HTML>").getBytes();
		};
	    if(pref==null)pref="HTTP/1.0 200 OK\nContent-Type: application/octet-stream\n\n".getBytes();
	    
	    OutputStream os=new BufferedOutputStream(s.getOutputStream());
	    os.write(pref,0,pref.length);
	    os.flush();

	    if(found)
		{
		    byte buf[]=new byte[16384];
		    int actual=0;
		    int avail=0;
		    while(true){
			avail=is.available();
			if (avail>0)
			    actual=is.read(buf);
			else
			    {
				os.flush();
				break;
			    }
			os.write(buf,0,actual);	
			Env.debug(50,"httpd "+file+" sent "+actual+" bytes" );
		    }
		}
	}catch(Exception e)
	    {
		Env.debug(20,"httpd "+file+" connection exception "+e.getMessage());
	    }
	finally{
	    try{
		Env.debug(50,"httpd "+file+" connection closing" );

		s.close();
	    }catch(Exception e){
	    };
	};
    };
    
    /**  
     *   this cuts the first line of the request into parts of the
     *   Request Notification so its easier to use
     */
    public void parseRequest(MetaProperties n)
    {
	String line=n.get("Request").toString();
	StringTokenizer st = new StringTokenizer(line);
	List list=new ArrayList();
	while (st.hasMoreTokens()) 
	    list.add( st.nextToken());
	n.put("Method",list.get(0).toString().intern());
        n.put("Resource",list.get(1).toString()); 
	n.put("Protocol",list.get(2).toString());

    }
    /** this is written to be over-ridden by the GateKeeper who looks
     * at registered URL specs.  by default it just sends a file it can find
     */

    public void dispatchRequest(Socket s,MetaProperties n)
    {

	//TODO: handle PUT , POST
	sendFile(s,n.get("Resource").toString());
    }

    /** sits and waits on a socket;
     */
    public void  run()
    { 
	while(true){
	    URL url=null;	
	    ArrayList list=new ArrayList(); 
	    try{
		Env.debug(20,"debug: "+Thread.currentThread().getName()+" init");
		Socket s=accept();
		MetaProperties n=getRequest(s);
		parseRequest(n);
		dispatchRequest(s,n);
	    }catch(Exception e){
		Env.debug(2,"HTTPServer thread going down in flames on : "+e.getMessage());
		e.printStackTrace();
	    };
	};
    };
}; 

