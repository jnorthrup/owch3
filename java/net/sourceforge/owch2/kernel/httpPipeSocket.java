package net.sourceforge.owch2.kernel;

import java.net.*;

/**
 * GateKeeper opens a httpPipeSocket to send data ussually in one
 * direction. $Id: httpPipeSocket.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @version $Id: httpPipeSocket.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public class httpPipeSocket extends net.sourceforge.owch2.kernel.PipeSocket {
    /**
     * pass in the requesting web socket, a MetaAgent with an URL defining the serving host/port, and a request stolen from a
     * GateKeeper incoming connection.  This literally opens up a proxy connection to said service and passes the information
     * along. TODO: look up simple web proxy semantics for connection headers.
     */
    public httpPipeSocket(Socket o, net.sourceforge.owch2.kernel.MetaAgent d,
                          net.sourceforge.owch2.kernel.MetaProperties request) {
        super(o);
        try {
            if (Env.logDebug) Env.log(15, "httpPipeSocket:httpPipeSocket using MetaAgent : " + d.toString());
            URLString u = new URLString(d.getURL() + request.get("Resource").toString());
            if (Env.logDebug) Env.log(15, "httpPipeSocket:httpPipeSocket using URL: " + u);
            request.put("Host", u.getHost() + ":" + u.getPort());
            isGet = request.get("Method").toString().equals("GET");
            //TODO: support file uploads.
            uc = new Socket(u.getHost(), u.getPort());
            connectTarget(uc);
            request.setFormat("RFC822");
            co.write((request.get("Request") + "\n").getBytes() );
            request.save(co);
            co.flush();
            spin();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ;
    };
}