package Cheetah;

import owch.Env;
import owch.Location;
import owch.MetaProperties;
import owch.Node;

/** ****************************************************************
 * application level interface to a parent routing node.
 *
 * @author Jim Northrup
 **************************************************************** */

public class Domain extends Node {

    public static void main( String[] args ) {
        java.util.Map m = Env.parseCmdLine( args );

        if ( !( m.containsKey( "JMSReplyTo" ) &&
        m.containsKey( "HostPort" ) ) ) {
            System.out.println(
            "\n\n******************** cmdline syntax error\n" +
            "Domain Agent usage:\n\n" +
            "-name name\n" +
            "-HostPort port\n" +
            "$Id: Domain.java,v 1.4 2001/09/23 10:20:10 grrrrr Exp $\n" );
            System.exit( 2 );
        };
        Env.setParentHost( true );
        Domain d = new Domain( m );
        Thread t = new Thread();

        try {
            t.start();
            while ( !d.killFlag ) {
                t.sleep( 60000 );
            }
        }
        catch ( Exception e ) {
        };
    };


    /** ************************************************************
     * Handles MetaPropertiess
     *
     ************************************************************ */

    public void handle_StartRoom( MetaProperties notificationIn ) {
        String msg = ( String )notificationIn.get(
        "MessageText" );

        if ( msg != null ) {
            java.util.StringTokenizer st =
            new java.util.StringTokenizer( msg );
            while ( st.hasMoreTokens() ) {
                startRoom( st.nextToken() );
            };
        };
        return;
    }



    /** ************************************************************
     * default ctor
     *
     ************************************************************ */
    public Domain( java.util.Map p ) {
        super( p );
        Env.getLocation( "owch" );
    };



    /** ************************************************************
     * Starts a room as part of the Domain's Server
     *
     ************************************************************ */

    public void startRoom( String name ) {

        if ( name != null ) {
            Env.debug( 5, "debug: Domain.startRoom(" +
            name + ")" );
            Location l = new Location();
            l.put( "JMSReplyTo", name );
            new Room( l );
        };
    };


    public final boolean isParent() {
        Thread.currentThread().yield();
        return true;
    }

};




