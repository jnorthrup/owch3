package owch;

import java.net.*;
import java.util.*;
import java.io.*;

public class HTTPServer extends TCPServerWrapper implements ListenerReference, Runnable
{
    public String getProtocol(){
	return "http";
    };
    public long getExpiration() {
	return (long) 0;
    }
    public int getThreads(){
	return 2;
    }
    public ServerWrapper getServer(){
	return this;
    };

    public void expire(){	
	getServer().close();
    };

    HTTPServer(int port)throws java.io.IOException{
	super(port);
	try{
	    new Thread(this).start();
	    new Thread(this).start();      
	}
	catch(Exception e){ Env.debug(2,"ServetrSocket creation Failure");
	};
    }; 
    /**called only on a new socket
     */   
    public String getRequest(Socket s ){
	/* expect:
GET /junk.html HTTP/1.1
Host: 10.21.12.1:35601
User-Agent: Mozilla/5.0 (X11; U; Linux 2.4.3-pre7 i686; en-US; m18) Gecko/20010131 Netscape6/6.01
Accept: * / *
Accept-Language: en
Accept-Encoding: gzip,deflate,compress,identity
Keep-Alive: 300
Connection: keep-alive

    */
	String request=""; 
	String line="";
        Env.debug(100,"HTTPServer.getRequest");
        try{
	   BufferedReader ins
	       = new BufferedReader(new InputStreamReader(s.getInputStream()));
	   do{
	       line=ins.readLine();
	       request=request+line;
	   }while(line.length()>0);
	     
	}catch(java.lang.Exception e){
	    Env.debug(5, "had a DynServer Snag, retry");
	}
	Env.debug(50, "returning "+request.toString());
	return request.toString();
    }
    public void  requestFile(Socket s,String file){
	//INHERIT ME
	/* sunsite  sent me these headers
	   HTTP/1.1 200 OK
	   Date: Thu, 29 Mar 2001 17:49:53 GMT
	   Server: Apache/1.3.19 (Unix) mod_jk mod_fastcgi/2.2.10 PHP/4.0.4pl1
	   Last-Modified: Fri, 22 Jan 1999 02:03:34 GMT
	   ETag: "d0074-22744-36a7dc76"
	   Accept-Ranges: bytes
	   Content-Length: 141124
	   Connection: close
	   Content-Type: application/octet-stream
	   Content-Encoding: x-gzip
	   */
	try{
	    byte[] pref="HTTP/1.1 200 OK\nContent-Type: application/octet-stream\n\n".getBytes();
	    
	    OutputStream os=new BufferedOutputStream(s.getOutputStream());
	    os.write(pref,0,pref.length);
	    
	    if(file.startsWith("/"))
		file=file.substring(1);
	    FileInputStream is=new FileInputStream(file);
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
		Env.debug(50,"httpd "+file+" sent "+avail+" bytes" );
	    }
	}catch(Exception e)
	    {
		Env.debug(20,"httpd "+file+" connection exception "+e.getMessage());
	    }
	finally{
	    try{
		Env.debug(50,"httpd "+file+" connection closing" );
		s.close();
	    }catch(Exception e){};
	};
    };

    public void  run()
    {
	while(true){
	    try{	
		Env.debug(20,"debug: "+Thread.currentThread().getName()+" init");
		Socket s=accept();
		new Thread(this).start();
		String req=getRequest(s);
		StringTokenizer st = new StringTokenizer(req);
		st.nextToken();
		String item=st.nextToken();
		requestFile(s,item);
		//	    while (st.hasMoreTokens()){
		//println(st.nextToken());
		//}	    
	    }catch(Exception e){
		Env.debug(2,"HTTPServer thread going down in flames");
	    };
	};
    }; 
};
