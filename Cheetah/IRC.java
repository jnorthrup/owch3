package Cheetah;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import owch.Env;
import owch.Location;
import owch.MetaProperties;
import owch.Node;

public class IRC extends Node implements Runnable {
    public int getIRCPort() {
        if ( this.containsKey( "IRCPort" ) ) {
            return Integer.decode(
            ( String )get( "IRCPort" ) ).intValue();
        }
        else {
            return 6667;
        }

    }

    /** ************************************************************
     *       this has the effect of taking over the
     * command of the http
     * service on the agent host and handling messages to marshal
     * http registrations
     ************************************************************ */
    public IRC( java.util.Map m ) {
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
                System.exit( 0 ); //TODO: allow our host to stay alive...
            };

            new Thread( this ).start();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        };
    };

    public static void main( String[] args ) {
        java.util.Map m = Env.parseCmdLine( args );
        if ( !(
        m.containsKey( "JMSReplyTo" ) &&
        m.containsKey( "IRCHost" ) &&
        m.containsKey( "IRCNickname" ) ) ) {
            System.out.println(
            "\n\n******************** cmdline syntax error\n" +
            "SocketProxy Agent usage:\n\n" +
            "-name       (String)name\n" +
            "-IRCHost (String)hostname/IP\n" +
            "-IRCNickname (String)nickname\n" +
            "[-IRCPort (int)port]\n" +
            "[-Clone 'host1[ ..hostn]']\n" +
            "[-Deploy 'host1[ ..hostn]']\n" +
            "$Id: IRC.java,v 1.1 2001/09/30 23:01:03 grrrrr Exp $\n" );
            System.exit( 2 );
        };
        IRC d = new IRC( m );
        d.spin();
    };

    public void spin() {
        Thread t = new Thread();
        try {
            t.start();
            while ( !killFlag ) {
                t.sleep( 6000 );
            } //todo: something
        }
        catch ( Exception e ) {
            e.printStackTrace();
        };
    }

    public void clone_state1( String host ) {
        MetaProperties n2 = new Location( this );
        n2.put( "JMSType", "DeployNode" );
        n2.put( "Class", getClass().getName() );
        n2.put( "JMSReplyTo", getJMSReplyTo() + "." + host );
        n2.put( "JMSDestination", host );
        send( n2 );
    };

    Socket irc;

    public void run() {
        try {
            String nick = get( "IRCNickname" ).toString();
            irc = new Socket( get( "IRCHost" ).toString(),
            this.getIRCPort() );
            BufferedReader is = new BufferedReader(
                new InputStreamReader( irc.getInputStream() ) );
            OutputStream os =
            irc.getOutputStream();
            Env.debug( 50, "IRC::USER" );
            String out =
             "USER " + nick + " " +
            getJMSReplyTo() + " " + getJMSReplyTo() + " :" +
            Env.getLocation("owch").getURL() + "\n";
            Env.debug( 50, "IRC::NICKNAME" );
            out = out + new String( "NICK " + nick + "\n" );
            Env.debug( 50, "IRC::SPINNING" );
            os.write( out.getBytes() );
            os.flush(); 
		Env.debug(50,"IRC::connect string "+out);
	   String line    ;

       boolean joined=false;
            while ( !this.killFlag ) {

                if(!joined){joined=true;
                String chans=get("IRCJoin").trim();
                }


                line    = is.readLine();
                if ( line.startsWith( "PING" ) ) {
                    String pong = "PONG " +line.substring( line.lastIndexOf( ":" ),line.length()  )+ "\n";
                    os.write( pong.getBytes() );
                    Env.debug(50,"IRC::" + pong );
                    os.flush();
                }
                Env.debug( 5, "IRC::" + line.trim() );
            }
        }
        catch ( Exception e ) {
            Env.debug( 5, this.getClass().getName() +
            "::connect threw " + e.getClass().getName() +
            "/" + e.getMessage() );
        };
    }

    public void handle_IRCJoinPublic  (MetaProperties m){

        StringReader sr=new StringReader(m.get("Channels").toString());
		
    }

}




