/*
Deploy.java

@author   Jim Northrup

$Log: Deploy.java,v $
Revision 1.4  2001/09/23 10:20:10  grrrrr
lessee

2 major enhancements

1) we now use reflection to decode message types.

a message looks for handle_<JMSType> method that takes a MetaProperties as its input

2) we now serve HTTP / 1.1 at every opportunity, sending content-length, and last-modified, and content type by default.  (WebPage still needs a few updates to catch up)

Revision 1.2  2001/04/27 12:47:54  grrrrr
webpages are functional, DeployAgent provides saner
means of cloning.

Revision 1.1.1.1  2001/04/26 03:06:12  grrrrr


Revision 1.2  2001/04/25 03:35:55  grrrrr
*** empty log message ***

Revision 1.1.1.1  2001/04/14 20:35:26  grrrrr


Revision 1.1.1.1  2001/04/12 19:07:26  grrrrr


*/
package Cheetah;

import java.net.URL;
import java.net.URLClassLoader;
import owch.Env;
import owch.MetaProperties;
import owch.Node;

public class Deploy extends Node {

    public static void main( String[] args ) {
        java.util.Map m = Env.parseCmdLine( args );

        if ( !( m.containsKey( "JMSReplyTo" ) ) ) {
            System.out.println(
            "\n\n******************** cmdline syntax error\n" +
            "Deploy Agent usage:\n\n" +
            "-name name\n" +
            "$Id: Deploy.java,v 1.4 2001/09/23 10:20:10 grrrrr Exp $\n" );
            System.exit( 2 );
        };

        Deploy d = new Deploy( m );
        Thread t = new Thread();
        t.start();
        while ( !d.killFlag ) {
            try {
                t.sleep( 60 * 60 * 3 );
            }
            catch ( Exception e ) {
            }
        }
    };

    /*
    *  Client Constructor
    *
    *  Initializes communication
    */


    public Deploy( java.util.Map map ) { super( map ); }

    /** ************************************************************
     *   Message: Deploy
     *
     *  Fields:
     *   Class - class to be constructed
     *   Path  - Array of URL Strings for Classpath, or "default"
     *  for native classloader
     *   Parameters  -  array of normalized Strings to pass into
     * our  new object
     ************************************************************ */

    public void handle_Deploy( MetaProperties n ) {

        String _class = ( String )n.get( "Class" );
        Env.debug( 45, "Deplying::Class " + _class );
        String path = ( String )n.get( "Path" );
        Env.debug( 45, "Deplying::Path " + path );
        String parm = ( String )n.get( "Parameters" );
        Env.debug( 45, "Deplying::Parameters " + parm );
        String signature = ( String )n.get( "Signature" );
        Env.debug( 45, "Deplying::Signature " + signature );
        int i;
        java.util.List tokens;

        //we use a classloader based on our reletive origin..
        ClassLoader loader = getClass().getClassLoader();


        String[] tok_arr = new String[] {
            path, parm, signature
        };
        Object[] [] res_arr = new Object[ tok_arr.length ] [];

        java.util.StringTokenizer st;
        for ( i = 0; i < tok_arr.length; i++ ) {
            String temp_str = tok_arr[ i ];
            tokens = new java.util.ArrayList();
            if ( temp_str == null ) {
                Env.debug( 500, "Deploy tokenizing nullinating  " + i );
                temp_str = "";
            }
            st = new java.util.StringTokenizer( temp_str );

            while ( st.hasMoreElements() ) {
                tokens.add( st.nextElement() );
            }

            res_arr[ i ] = tokens.toArray();
            Env.debug( 500, "Deploy arr" + i + " found " +
            res_arr[ i ].length + " tokens" );
        }

        URL[] path_arr = new URL[ res_arr[ 0 ].length ];
        Object[] parm_arr = new Object[ res_arr[ 1 ].length ];
        Class[] sig_arr = new Class[ res_arr[ 2 ].length ];

        try {
            //path is URL's, gotta do a loop to instantiate URL's...
            for ( i = 0; i < res_arr[ 0 ].length; i++ ) {
                path_arr[ i ] = new
                    URL( ( String )res_arr[ 0 ] [ i ] );
            }


            //parms are strings, easy copy there...
            System.arraycopy( res_arr[ 1 ], 0, parm_arr, 0,
            res_arr[ 1 ].length );

            //determine if our loader is based on URLS....
            if ( path_arr.length != 0 ) //if we have URL's we need
            // to make a new loader that adds these URLS to our path
            {
                loader = new URLClassLoader( path_arr,
                loader );
            }

            //user our loader to populate sig_arr with a Class[]
            for ( i = 0; i < res_arr[ 2 ].length; i++ ) {
                sig_arr[ i ] = loader.loadClass(
                ( String )res_arr[ 2 ] [ i ] );
            }


            /* this creates a new Object */
            loader.loadClass( _class ).getConstructor( sig_arr
                ).newInstance( parm_arr );
            return;
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void handle_DeployNode( MetaProperties n ) {

        String _class = ( String )n.get( "Class" );
        Env.debug( 45, "Deplying::Class " + _class );
        String path = ( String )n.get( "Path" );
        Env.debug( 45, "Deplying::Path " + path );
        int i;
        java.util.List tokens;

        //we use a classloader based on our reletive origin..
        ClassLoader loader = getClass().getClassLoader();

        String[] tok_arr = new String[] {
            path,
        };
        URL[] path_arr = new URL[] {
        };
        Object[] [] res_arr = new Object[ tok_arr.length ] [];

        java.util.StringTokenizer st;
        for ( i = 0; i < tok_arr.length; i++ ) {
            String temp_str = tok_arr[ i ];
            tokens = new java.util.ArrayList();
            if ( temp_str == null ) {
                Env.debug( 500, "Deploy tokenizing nullinating  " + i );
                temp_str = "";
            }
            st = new java.util.StringTokenizer( temp_str );

            while ( st.hasMoreElements() ) {
                tokens.add( st.nextElement() );
            }

            res_arr[ i ] = tokens.toArray();
            Env.debug( 500, "Deploy arr" + i + " found " +
            res_arr[ i ].length + " tokens" );
        }
        try {
            //path is URL's, gotta do a loop to instantiate URL's...
            for ( i = 0; i < res_arr[ 0 ].length; i++ ) {
                path_arr[ i ] = new
                URL( ( String )res_arr[ 0 ] [ i ] );
            }

            /* this creates a new Object */

            loader.loadClass( _class ).getConstructor(
                new Class[] {
                    java.util.Map.class
                } ).newInstance(
                new Object[] {
                    n
                } );
            //use our Notification as a bootstrap of parms
            return;
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}




