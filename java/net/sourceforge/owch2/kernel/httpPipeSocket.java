package net.sourceforge.owch2.kernel;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.util.logging.Logger;

/**
 * GateKeeper opens a httpPipeSocket to route data ussually in one
 * direction. $Id$
 *
 * @author James Northrup
 * @version $Id$
 */
public class httpPipeSocket extends PipeSocket {
    protected static final Object REQUEST_KEY = "Request";
    protected static final Object METHOD_TYPE_GET = "GET";
    protected static final String RESOURCE_KEY = "Resource";
    protected static final Object METHOD_KEY = "Method";

    /**
     * pass in the requesting web socket, a EventDescriptor with an URL defining the serving host/port, and a request stolen from a
     * GateKeeper incoming connection.  This literally opens up a proxy connection to said service and passes the information
     * along. TODO: look up simple web proxy semantics for connection headers.
     */
    public httpPipeSocket(Socket socket, EventDescriptor EventDescriptor, EventDescriptor request) {
        super(socket);
        try {
            Logger.getAnonymousLogger().info("using EventDescriptor : " + EventDescriptor.toString());

            URI uri = URI.create(String.valueOf(EventDescriptor.getURI().toASCIIString() + request.get(AbstractAgent.RESOURCE_KEY)));
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