package owch;

import java.net.*;
import java.util.*;
import java.io.*;



/**
 *  GateKeeper opens a httpPipeSocket to send data ussually in one
 *  direction.
 $Id: httpPipeSocket.java,v 1.3 2001/09/23 10:20:10 grrrrr Exp $
 * @version $Id: httpPipeSocket.java,v 1.3 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup
 */
public class httpPipeSocket extends PipeSocket
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
    
    public httpPipeSocket(Socket o, MetaNode d, MetaProperties request) { 
	super(o);
        try { 

	    Env.debug(15,"httpPipeSocket:httpPipeSocket using MetaNode : "+d.toString());
            URLString u =new URLString( d.getURL()+request.get("Resource").toString());
            Env.debug(15,"httpPipeSocket:httpPipeSocket using URL: "+u);
	    request.put("Host",u.getHost()+":"+u.getPort());
	    isGet=request.get("Method").toString().equals("GET");
	    
	    //TODO: support file uploads.
	    uc=new Socket(u.getHost(),u.getPort());

	    connectTarget(uc);

	    request.setFormat("RFC822");
	    co.write((request.get("Request").toString()+'\n').getBytes()); 
	    request.save(co); 
	    co.flush();
	    spin();
	} catch(Exception e){ 
	       e.printStackTrace();
	    
	};
    };
};
//$Log: httpPipeSocket.java,v $
//Revision 1.3  2001/09/23 10:20:10  grrrrr
//lessee
//
//2 major enhancements
//
//1) we now use reflection to decode message types.
//
//a message looks for handle_<JMSType> method that takes a MetaProperties as its input
//
//2) we now serve HTTP / 1.1 at every opportunity, sending content-length, and last-modified, and content type by default.  (WebPage still needs a few updates to catch up)
//
//Revision 1.1.2.1  2001/04/30 04:27:56  grrrrr
//SocketProxy + Deploy methods
//
