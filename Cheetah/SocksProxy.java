package Cheetah;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import owch.Env;
import owch.Location;
import owch.MetaProperties;
import owch.Node;
import owch.PipeFactory;
import owch.PipeSocket;


public class SocksProxy extends Node implements Runnable {
    ServerSocket ss;

    final static String[] errs = new String[] {
        "succeeded",
        "general SOCKS server failure",
        "connection not allowed by ruleset",
        "Network unreachable",
        "Host unreachable",
        "Connection refused",
        "TTL expired",
        "Command not supported",
        "Address type not supported",
        "to X'FF' unassigned",
    };

    public static void main( String[] args ) {
        java.util.Map m = Env.parseCmdLine( args );
        if ( !(
        m.containsKey( "JMSReplyTo" ) &&
        m.containsKey( "SocksHost" ) &&
        m.containsKey( "SourcePort" ) &&
        m.containsKey( "SourceHost" ) &&
        m.containsKey( "ProxyPort" ) ) ) {
            System.out.println(
            "\n\n******************** cmdline syntax error\n" +
            "SocketProxy Agent usage:\n\n" +
            "-name       (String)name\n" +
            "-SourceHost (String)hostname/IP\n" +
            "-SocksHost (String)hostname/IP\n" +
            "-SourcePort (int)port\n" +
            "-ProxyPort  (int)port\n" +
                //            "[-SocksAuth (String)User/Password]\n" +
                "[-SocksPort (int)port]\n" +
                "[-Clone 'host1[ ..hostn]']\n" +
                "[-Deploy 'host1[ ..hostn]']\n" +
                "$Id: SocksProxy.java,v 1.1 2001/09/30 05:47:42 grrrrr Exp $\n" );
            System.exit( 2 );
        };
        SocketProxy d = new SocketProxy( m );
        Thread t = new Thread();
        try {
            t.start();
            while ( !owch.Env.shutdown ) {
                t.sleep( 6000 );
            } //todo: something
        }
        catch ( Exception e ) {
            e.printStackTrace();
        };

    };


    public int getSourcePort() {
        return Integer.decode( ( String )get( "SourcePort" )
            ).intValue();
    }

    public int getSocksPort() {
        if ( this.containsKey( "SocksPort" ) ) {
            return Integer.decode(
            ( String )get( "SocksPort" ) ).intValue();
        }
        else {
            return 1080;
        }

    }

    public int getProxyPort() {
        return Integer.decode( ( String )get( "ProxyPort" )
            ).intValue();
    }

    /** handy remote deployment code */
    public SocksProxy( java.util.Map m ) {
        super( m );
        try {
            if ( containsKey( "Clone" ) ) {
                String clist = ( String )get( "Clone" );
                remove( "Clone" );
                Env.debug( 500, getClass().getName() +
                " **Cloning for " + clist );
                java.util.StringTokenizer st =
                new java.util.StringTokenizer( clist );
                while ( st.hasMoreTokens() ) {
                    clone_state1( st.nextToken() );
                }
            };
            if ( containsKey( "Deploy" ) ) {
                String clist = ( String )get( "Deploy" );
                remove( "Deploy" );
                Env.debug( 500, getClass().getName() +
                " **Cloning for " + clist );
                java.util.StringTokenizer st =
                new java.util.StringTokenizer( clist );
                while ( st.hasMoreTokens() ) {
                    clone_state1( st.nextToken() );
                }
                Thread.currentThread().sleep( 15 * 1000 ); //kludge,
                // allow udp messages to arrive...
                handle_Dissolve( null ); //TODO: allow our host to
                // stay alive...

                owch.Env.shutdown = true;
            };
            ss = new ServerSocket( getProxyPort() );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        };
    };

    PipeFactory pf = new PipeFactory();
    private int socksPort = 1080;

    /** ************************************************************
     *The client connects to the server, and sends a version *
     *identifier/method selection message:
     *
     *<TABLE BORDER WIDTH=1>
     *<TR> <TH>VER <TH>NMETHODS <TH> METHODS </TR>
     *<TR><TD>1<TD>1 <TD> 1 to 255 </TABLE>
     *
     *<P> The VER field is set to X'05' for this version of
     * the protocol.
     *The NMETHODS field contains the number of method
     * identifier octets
     *that appear in the METHODS field.  The server selects
     * from one of the
     *methods given in METHODS, and sends a METHOD
     * selection message:
     *
     *<TABLE BORDER WIDTH=1>
     *<TR><TH>VER <TH>METHOD </TR>
     *<TR><TD>1 <TD>1 </TR></TABLE>
     *
     *<P>If the selected METHOD is X'FF', none of the methods
     * listed by the
     *client are acceptable, and the client MUST close the
     * connection.  The
     *values currently defined for METHOD are:
     *
     *<TABLE BORDER WIDTH=1>
     *<TR><TD>X'00'TD<TD> NO AUTHENTICATION REQUIRED</TR>
     *<TR><TD>X'01'<TD> GSSAPI</TR>
     *<TR><TD>X'02' <TD>USERNAME/PASSWORD </TR>
     *<TR><TD>X'03' to X'7F' <TD>IANA ASSIGNED </TR>
     *<TR><TD>X'80' to X'FE'<TD> RESERVED FOR PRIVATE
     * METHODS </TR>
     *<TR><TD> X'FF' <TD>NO ACCEPTABLE METHODS </TABLE>
     *
     ************************************************************ */
    public void run() {
        while ( !killFlag ) {
            try {
                //todo: time out somehow
                Socket inbound = ss.accept(); //wait for connection on ProxyPort
                PipeSocket ps = new PipeSocket( inbound );
                Socket socks =
                new Socket( ( String )get( "SocksHost" ),
                getSocksPort() );

                byte methods[] = new byte[] {
                    0 //,2
                };
                byte ver = 5, nmethods = ( byte )methods.length;

                socks.getOutputStream().write(
                    new byte[] {
                        ver, nmethods
                    } );
                sockshost.getOutputStream().write( methods );

                byte resp[] = new byte[ 2 ];

                sockshost.getInputStream().read();

                if ( !( resp[ 0 ] == 5 && resp[ 1 ] == 0 ) ) {
                    Env.debug( 2, this.getClass().getName() +
                    " Socks proxy failures returned other than socks5 Auth0; aborting  " );
                    inbound.close();

                    return;
                };

                this.send_request( socks )
                if ( this.handle_response( socks ) ) {
                    ps.connectTarget( socks );
                    ps.spin();
                }
            }
            catch ( InterruptedIOException e ) {
                Env.debug( 500, getClass().getName() +
                "::interrupt " + e.getMessage() );
            }
            catch ( Exception e ) {
                Env.debug( 10, getClass().getName() + "::run " +
                e.getMessage() );
                e.printStackTrace();
            };
        };
    };

    public void clone_state1( String host ) {
        MetaProperties n2 = new Location( this );
        n2.put( "JMSType", "DeployNode" );
        n2.put( "Class", getClass().getName() );
        n2.put( "JMSReplyTo", getJMSReplyTo() + "." + host );
        //resource remains constant in this incarnation
        //n2.put( "Resource",get("Resource"));//produces 3 Strings
        n2.put( "JMSDestination", host );
        send( n2 );
    };

    /** ************************************************************
     *
     *         <p> Requests Once the method-dependent
     * subnegotiation has
     *         completed, the client sends the request
     * details.  If the
     *         negotiated method includes encapsulation
     * for purposes of
     *         integrity checking and/or confidentiality,
     * these requests
     *         MUST be encapsulated in the method- dependent
     * encapsulation.
     *
     *         <p>The SOCKS request is formed as follows: <P>
     *<pre>
<TABLE BORDER WIDTH=1>
<TR> <TH>VER <TH>CMD <TH>RSV  <TH>ATYP <TH>DST.ADDR<TH>DST.PORT </TR>
<TR> <TD>1   <TD>1   <TD>X'00'<TD>1    <TD>Variable<TD>2 </TABLE>
     *
     *
     <P>
        Where:<UL>
               <LI>VER    protocol version: X'05'
               <LI>CMD<UL>
                  <LI>CONNECT X'01'
                  <LI>BIND X'02'
                  <LI>UDP ASSOCIATE X'03'</UL>
               <LI>RSV    RESERVED
               <LI>ATYP   address type of following address
                  <UL><LI>IP V4 address: X'01'
                  <LI>DOMAINNAME: X'03'
                  <LI>IP V6 address: X'04'</UL>
               <LI>DST.ADDR       desired destination address
               <LI>DST.PORT desired destination port in network octet order </UL>
     *
     *
                  <P>The SOCKS server will typically evaluate the
                  request based on source and destination addresses,
                  and return one or more reply messages, as
                  appropriate for the request type. */
    void send_request( Socket socks ) {
        try {
            DataOutputStream os =
            new DataOutputStream( socks.getOutputStream() );
            short sport = ( short )this.getSourcePort();

            byte VER = 5,
            CMD = 1, //CONNECT
                RSV = 0,
                ATYP = 3,
                DST_ADDR[] = get( "SourceHost" ).toString().getBytes(),
                DST_ADDR_LEN = ( byte )DST_ADDR.length;
            os.write( VER );
            os.write( CMD );
            os.write( RSV );
            os.write( ATYP );
            os.write( DST_ADDR_LEN );
            os.write( DST_ADDR );
            os.write( sport );
        }
        catch ( Exception e ) {
            Env.debug( 2, this.getClass().getName() +
            "::handle_socks_reply threw " +
            e.getClass().getName() + "/" + e.getMessage() );

        }
    }

    /** ************************************************************
     *<PRE>
     *
   The SOCKS request information is sent by the client as soon as it has
   established a connection to the SOCKS server, and completed the
   authentication negotiations.  The server evaluates the request, and
   returns a reply formed as follows:
   <P>
      <TABLE BORDER WIDTH=1>
      <TR><TH>
VER <TH>REP<TH> RSV <TH> ATYP <TH> BND.ADDR <TH> BND.PORT </TR>
<TR><TD>1  <TD> 1  <TD> X'00' <TD>  1   <TD> Variable<TD>    2  </TABLE>
     *
<P>
        Where:<UL>
          <LI>VER    protocol version: X'05'
          <LI>REP    Reply field:
             <UL>
          <LI>  X'00' succeeded
          <LI>  X'01' general SOCKS server failure
             <LI>  X'02' connection not allowed by ruleset
             <LI> X'03' Network unreachable
             <LI>  X'04' Host unreachable
             <LI>  X'05' Connection refused
             <LI>  X'06' TTL expired
             <LI>  X'07' Command not supported
             <LI>  X'08' Address type not supported
             <LI>  X'09' to X'FF' unassigned</UL>
          <LI>  RSV    RESERVED
          <LI>ATYP   address type of following address
          <UL><LI>  IP V4 address: X'01'
             <LI>DOMAINNAME: X'03'
             <LI>IP V6 address: X'04'<UL>
          <LI>BND.ADDR       server bound address
          <LI>BND.PORT       server bound port in network octet order
</UL></UL>
          <P>Fields marked RESERVED (RSV) must be set to X'00'.
          If the chosen method includes encapsulation for purposes of
   authentication, integrity and/or confidentiality, the replies are
   encapsulated in the method-dependent encapsulation.
     **/
    public boolean handle_response( Socket socks ) {

        try {
            DataInputStream is =
            new DataInputStream( socks.getInputStream() );

            //  |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
            byte VER = ( byte )is.read(),
            REP = ( byte )is.read(),
            RSV = ( byte )is.read(),
            ATYP = ( byte )is.read(),
            BND_ADDR_LEN = ( byte )is.read(),
            BND_ADDR[] = new byte[ BND_ADDR_LEN ];

            is.read( BND_ADDR );

            short BND_PORT = is.readShort();

            Env.debug( 15, this.getClass().getName() +
            "::Connect request returned "
            + " VER:" + ( int )VER
            + " REP:" + errs[ REP ]
            + " ATYP:" + ( int )ATYP
            + " " + new String( BND_ADDR )
            + " BND_PORT:" + BND_PORT );
            return ( REP == 0 );
        }
        catch ( Exception e ) {
            Env.debug( 2, this.getClass().getName() +
            "::handle_socks_reply threw " +
            e.getClass().getName() + "/" + e.getMessage() );
            return false;
        }
    }
}
//$Log: SocksProxy.java,v $
//Revision 1.1  2001/09/30 05:47:42  grrrrr
//socks proxy written but untested
//
//Revision 1.3  2001/09/23 10:20:10  grrrrr
//lessee
//
//2 major enhancements

//
//1) we now use reflection to decode message types.
//
//a message looks for handle_<JMSType> method that takes a
// MetaProperties as its input
//
//2) we now serve HTTP / 1.1 at every opportunity, sending
// content-length, and last-modified, and content type by
// default.  (WebPage still needs a few updates to catch up)
//
//Revision 1.1.2.1  2001/04/30 04:27:56  grrrrr
//SocketProxy + Deploy methods
//




