package owch;

import java.net.*;
import java.util.*;
import java.io.*;



/**
 *  GateKeeper opens a PipeSocket to send data ussually in one
 *  direction.
 */
public class PipeSocket
{ 
    

    /** pass in the requesting web socket, a MetaNode with an URL
      * defining the serving host/port, and a request stolen from a
      * GateKeeper incoming connection.  This literally opens up a
      * proxy connection to said service and passes the information
      * along.
      *
      * TODO: look up simple web proxy semantics for connection
      * headers.
      */
    
    public PipeSocket(Socket o, MetaNode d, MetaProperties request) { 
        try { 
	    //for GET method the inner socket OutputStream will close
	    //first.
	    boolean isGet=false; 
	    //for PUT method the outer socket InputStream will close
	    //first.
	    boolean isPut=false; 
	    Socket uc;
	    InputStream oi;
	    InputStream ci;
	    OutputStream oo;
	    OutputStream co;
	    Env.debug(15,"PipeSocket:PipeSocket using MetaNode : "+d.toString());
            URLString u =new URLString( d.getURL()+request.get("Resource").toString());
            Env.debug(15,"PipeSocket:PipeSocket using URL: "+u);
	    request.put("Host",u.getHost()+":"+u.getPort());
	    isGet=request.get("Method").toString().equals("GET");
	    
	    //TODO: support file uploads.
	    uc=new Socket(u.getHost(),u.getPort());
	    o. setReceiveBufferSize(32*1024);	
	    uc. setReceiveBufferSize(32*1024);
	    o. setSendBufferSize(32*1024);	
	    uc. setSendBufferSize(32*1024);

            oi =new BufferedInputStream(    o.getInputStream()) ; 
	    ci =new BufferedInputStream(    uc.getInputStream()) ;
	    oo =new BufferedOutputStream(   o.getOutputStream()) ;
	    co =new BufferedOutputStream(   uc.getOutputStream()) ; 
	    
	    request.setFormat("RFC822");
	    co.write((request.get("Request").toString()+'\n').getBytes()); 
	    request.save(co);
	    co.flush();

	    
	    ThreadGroup tg=new ThreadGroup("TG:"+request.get("Resource").toString());
	    new PipeThread(tg,uc,oi,co,true,"PTInput");//
	    new PipeThread(tg,o,ci,oo,true,"PTOutput"+request.get("Resource").toString());//

	} catch(Exception e){ 
	};
    };
	
    /**   worker thread for the PipeSocket.  symetrical.java has a bug
      *   which restricts socket closes to be all or
      *   nothing. therefore we cannot close "half" of our pipe
      *   without killing the other half.  This means that "terminate"
      *   booleans are to indicate whether the stream wille cose the
      *   pipe when it hits EOF or will wait for the other side
      *   indefinitely. Sun says "NOT A BUG".  its a good idea
      *   therefore to use a C web server or proxy.  :-(
      *
      */

    public class PipeThread implements Runnable{
	InputStream is;
	OutputStream os;
	boolean term;
	int actual, avail;
	Object pipe;
	final int blocksize=18 * 1024;
	byte[] buf=new byte[blocksize];
	String label;
	ThreadGroup tg;
	
	public PipeThread(ThreadGroup t,Object closeable,InputStream i,OutputStream o,boolean terminate,String name)
	{
	    tg=t;
	    pipe=closeable;
	    is=i;
	    os=o;
	    term=terminate;
	    label=name;
	    new Thread(this,label).start();
	};
	/**worker thread 
	 */
	public void run()
	{
	    try{
		while(true){ //spin around indefinitely
		    for( avail=is.available();avail>0;){//runs while
							//data exists
							//to be
							//claimed
		    Env.debug(500, label+" read has available bytes: " + avail); 
		   
		    actual = is.read(buf);
		    if(actual==-1){
			os.flush();
			Env.debug(15, label +" input stream closed " + actual); 
			if(term){
			    os.close();
			    //close something... 
			    pipe.getClass().getMethod("close",new Class[]{}).invoke(pipe,new Object[]{});
			    //interrupt our sister thread... which
			    //should be asleep
			    tg.interrupt();
			};
			return;
		    }; 
		    Env.debug(500, label+" output: " + actual);
		    os.write(buf,0,actual);
		    };
		    //we avoid blocking in case we need to be
		    //interrupted by our sister thread.
		    Thread.currentThread().sleep(100);
		};
	    }catch (Exception e)
		{
		    Env.debug(500, label+" closing: "+e.getMessage());
		}
	};
    };
};
