package net.sourceforge.owch2.kernel;

import java.io.*;
import java.net.*;
import java.util.logging.*;

/**
 * GateKeeper opens a httpPipeSocket to send data ussually in one
 * direction. $Id: httpPipeSocket.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 *
 * @author James Northrup
 * @version $Id: httpPipeSocket.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class httpPipeSocket extends PipeSocket {
    protected static final Object REQUEST_KEY = "Request";
    protected static final Object METHOD_TYPE_GET = "GET";
    protected static final String RESOURCE_KEY = "Resource";
    protected static final Object METHOD_KEY = "Method";

    /**
     * pass in the requesting web socket, a MetaAgent with an URL defining the serving host/port, and a request stolen from a
     * GateKeeper incoming connection.  This literally opens up a proxy connection to said service and passes the information
     * along. TODO: look up simple web proxy semantics for connection headers.
     */
    public httpPipeSocket(Socket socket, MetaAgent metaAgent, MetaProperties request) {
        super(socket);
        try {
            Logger.getAnonymousLogger().info("using MetaAgent : " + metaAgent.toString());

            URI uri = URI.create(metaAgent.getURI() + request.get(AbstractAgent.RESOURCE_KEY));
            Logger.getAnonymousLogger().info("using URL: " + uri);
            request.put(AbstractAgent.MOBILEHOST_KEY, uri.getHost() + ":" + uri.getPort());
            isGet = request.get(METHOD_KEY).equals(METHOD_TYPE_GET);
            //TODO: support file uploads.
            uc = new Socket(uri.getHost(), uri.getPort());
            connectTarget(uc);
            co.write((request.get(REQUEST_KEY) + "\n").getBytes());
            request.save(co);
            co.flush();
            spin();
        } catch (IOException e) {
            e.printStackTrace();  //!TODO: review for fit
        }
    }
}