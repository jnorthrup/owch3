package owch;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;


/** ****************************************************************
 *  Http server daemon used for sending files and routing agent
 * notifications.
 * @version $Id: httpServer.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup
 **************************************************************** */
public class httpServer extends TCPServerWrapper implements
ListenerReference, Runnable {
    int threads;

    public String getProtocol() { return "http"; };

    public long getExpiration() { return ( long )0; }

    public int getThreads() { return threads; }

    public ServerWrapper getServer() { return this; };

    public void expire() { getServer().close(); };

    public httpServer( int port, int threads )
    throws IOException {
        super( port );
        this.threads = threads;
        try {
            for ( int i = 0; i < threads; i++ ) {
                    new Thread( this ).start();
            }
        }
        catch ( Exception e ) {
            Env.debug( 2, "httpServer creation Failure" );
        };
    };

    /** called only on a new socket */
    public MetaProperties getRequest( Socket s ) {
        String line = "";
        Env.debug( 100, "httpServer.getRequest" );
        Notification n = new Notification();
        try {
            n.setFormat( "RFC822" );
            DataInputStream ins =
            new DataInputStream( s.getInputStream() );
            line = ins.readLine();
            n.load( ins );
            n.put( "Request", line );
        }
        catch ( Exception e ) {
            Env.debug( 5, "had a DynServer Snag, retry" );
        }
        Env.debug( 50, "returning " + n.toString() );
        return n;
    }

    /** default action of an agent host is to just send a file. */
    public void sendFile( Socket s, String file ) {
        /** ********************************************************
         * errors would send...
         *         HTTP/1.1 404 Not Found
         *         Date: Sun, 08 Apr 2001 21:31:24 GMT
         * 		   Server: Apache/1.3.12 (Unix) mod_perl/1.24
         *         Connection: close
         *         Content-Type: text/html; charset=iso-8859-1
         ******************************************************** */
        try {
            boolean found = true;
            byte[] pref = null;
            if ( file.startsWith( "/" ) ) {
                file = file.substring( 1 );
            }
            FileInputStream is = null;
            File fd = null;
            try {
                fd = new File( file );
                is = new FileInputStream( file );
            }
            catch ( Exception e ) {
                found = false;
                pref = new String( "HTTP/1.1 404 " + e.getMessage() +
                "\nConnection: close\n\n<!DOCTYPE HTML PUBLIC -//IETF//DTD HTML 2.0//EN><HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY><H1>" +
                e.getMessage() + "</H1>The requested URL " + file +
                " was not found on this server.<P></BODY></HTML>"
                    ).getBytes();
            };
            if ( pref == null ) {
                FileInputStream i = ( FileInputStream )is;
                String p = "HTTP/1.1 200 OK\n" +
                "Content-Type: " + Env.getContentType( file ) +
                "\n" +
                "Last-Modified: " + new java.text.SimpleDateFormat().format(
                    new java.util.Date( fd.lastModified() ) ) +"\n"+
                    "Content-Length: " + fd.length() +
                    "\n\n";


                pref = p.getBytes();
            };
            OutputStream os =
            new BufferedOutputStream( s.getOutputStream() );
            os.write( pref, 0, pref.length );
            os.flush();
            if ( found ) {
                byte buf[] = new byte[ Math.min(32*1024,(int)fd.length())];
                int actual = 0;
                int avail = 0;
                while ( true ) {
                    avail = is.available();
                    if ( avail > 0 ) {
                        actual = is.read( buf );
                    }
                    else {
                        os.flush();
                        break;
                    }
                    os.write( buf, 0, actual );
                    Env.debug( 50,
                    "httpd " + file + " sent " + actual +
                    " bytes" );
                }
            }
        }
        catch ( Exception e ) {
            Env.debug( 20, "httpd " + file +
            " connection exception " + e.getMessage() );
        }
        finally {
            try {
                Env.debug( 50, "httpd " + file +
                " connection closing" );
                s.close();
            }
            catch ( Exception e ) {
            };
        };
    };

    /** ************************************************************
     * this cuts the first line of the request into parts of
     * the Request Notification so its easier to use
     ************************************************************ */
    public void parseRequest( MetaProperties n ) {
        String line = n.get( "Request" ).toString();
        java.util.StringTokenizer st =
        new java.util.StringTokenizer( line );
        java.util.List list = new java.util.ArrayList();
        while ( st.hasMoreTokens() ) {
            list.add( st.nextToken() );
        }
        n.put( "Method", list.get( 0 ).toString().intern() );
        n.put( "Resource", list.get( 1 ).toString() );
        n.put( "Protocol", list.get( 2 ).toString() );
    }

    /** ************************************************************
     * this is written to be over-ridden by the
     * GateKeeper who looks
     * at registered URL specs.  by default it just sends a
     * file it can find
     ************************************************************ */
    public void dispatchRequest( Socket s, MetaProperties n ) {
        if ( Env.gethttpRegistry().dispatchRequest( s, n )
        == false ) {
            sendFile( s, n.get( "Resource" ).toString() );
        }
    }

    /** sits and waits on a socket; */
    public void run() {
        while ( !Env.shutdown ) {
            URL url = null;
            java.util.ArrayList list = new java.util.ArrayList();
            try {
                Env.debug( 20, "debug: " +
                Thread.currentThread().getName() +
                " init" );
                Socket s = accept();
                MetaProperties n = getRequest( s );
                parseRequest( n );
                dispatchRequest( s, n );
            }
            catch ( Exception e ) {
                Env.debug( 2, "httpServer thread going down in flames on : " +
                e.getMessage() );
                e.printStackTrace();
            };
        };
    };
};




