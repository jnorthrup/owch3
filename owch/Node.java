package owch;

/** ****************************************************************
 *    Node provides the base class which communicates withthe Env
 * agent host platform and the
 * protocols it operates. communication is handled by constructing
 * a MetaProperties Object and calling the send() method
 * of the Node.  The Env Host platform manages the details of
 * protocols, routing, and delivery to
 * other nodes in the namespace.
 * @version $Id: Node.java,v 1.4 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup
 **************************************************************** */
abstract public class Node extends java.util.TreeMap
implements MetaNode {
    protected boolean killFlag = false;
    boolean virgin;
    LinkRegistry acl = null;

    private final static Class[] cls_m = new Class[] {
        MetaProperties.class
    };


    /** ************************************************************
     * this tells our (potentially clone) web page to stop
     * re-registering.  it will cease to spin.
     ************************************************************ */
    public void handle_Dissolve(MetaProperties n) {
        killFlag = true;
        //UnLink("default")
    };

    public boolean isParent() { return false; };


    public final void receive( MetaProperties n ) {
        String JMSType = ( String )n.get( "JMSType" );
        try {
            getClass().getMethod( "handle_" + JMSType,
            cls_m ).invoke( this,
                new Object[] {
                    n
                } );


        }

        catch (  Exception e ) {
            Env.debug( 2, e.getMessage()+" thrown for " +
            this.getJMSReplyTo() + "::"+getClass().getName() +"->"+ JMSType );
        };
    }

    public void handle_Link( MetaProperties p ) {
        String dest=p.getJMSReplyTo();
        MetaProperties n = new Notification();
        n.put( "JMSType", "Update" );
        n.put( "JMSDestination", dest );
        send( n );
        Env.debug( 15, getClass().getName() + "::" +
        getJMSReplyTo() +
        " Node.update() sent for " + dest );
    };


    /** ************************************************************
     *  Sends an update to another Node
     *  @param dest JMSReplyTo
     ************************************************************ */
    public void handle_Update( MetaProperties p  ) {
        String dest=p.getJMSReplyTo();
        MetaProperties n = new Notification();
        n.put( "JMSType", "Updated" );
        n.put( "JMSDestination", dest );
        send( n );
        Env.debug( 15, getClass().getName() + "::" +
        getJMSReplyTo() +
        " Node.update() sent for " + dest );
    }
   /** ************************************************************
     *  Sends an update to another Node
     *  @param dest JMSReplyTo
     ************************************************************ */
    public void handle_Updated( MetaProperties p  ) {

    }

    public Node() { }

    public Node( java.util.Map p ) {
        super( p );
        Env.getRouter( "IPC" ).addElement( this );
        if ( !isParent() ) {
            linkTo( "default" );
        }
    }

    public void init( java.util.Map p ) {
        putAll( p );
        Env.getRouter( "IPC" ).addElement( this );
        if ( !isParent() ) {
            linkTo( "default" );
        }
    }

    /** ************************************************************
     * Sends a Link notification other node(s) intended to
     * establish direct socket communication.
     * @param lk node(s) to link to
     ************************************************************ */
    public void linkTo( String lk ) {
        if ( lk == null ) {
            Env.debug( 5, getClass().getName() + "::" +
            this.getJMSReplyTo() +
            ".link invoked. routing to default" );
            lk = Env.getParentNode().getJMSReplyTo();
        };
        MetaProperties n = new Notification();
        n.put( "JMSDestination", lk );
        n.put( "JMSType", "Link" );
        send( n );
    }

    /** ************************************************************
     * Sends a Link notification other node(s) intended to
     * establish direct socket communication.
     * @param lk node(s) to link to
     ************************************************************ */
    public void handle_Unlink( MetaProperties m ) {
        String lk=m.getJMSReplyTo();
        if ( lk == null ) {
            Env.debug( 5, getClass().getName() + "::" +
            this.getJMSReplyTo() +
            ".unlink invoked. routing to default" );
            lk = Env.getParentNode().getJMSReplyTo();
        };
        MetaProperties n = new Notification();
        n.put( "JMSDestination", lk );
        n.put( "JMSType", "UnLink" );
        send( n );
    }

    /** ************************************************************
     * Node level Notification insignia creation and
     * inter-process notification routing.
     * @param n  Notification destined for somewhere else
     ************************************************************ */
    public void send( MetaProperties n ) {
        String d = null;
        String w = null;
        Node node = null;
        //see if this works out
        if ( n.getJMSReplyTo() == null ) {
            n.put( "JMSReplyTo", this.getJMSReplyTo() );
        };
        d = ( String )n.get( "JMSDestination" );
        if ( d == null ) {
            Env.debug( 8, "debug: Node.Send(Notification) dropping unsendd Notification from " +
            getJMSReplyTo() );
            return;
        };
        Env.send( n );
    };

    public final String getURL() {
        String s = ( String )get( "URL" );
        return s;
    }

    public final String getJMSReplyTo() {
        return ( String )get( "JMSReplyTo" );
    };
};
//$Log: Node.java,v $
//Revision 1.4  2001/09/23 10:20:10  grrrrr
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
//Revision 1.1.1.1.2.1  2001/04/30 04:27:56  grrrrr
//SocketProxy + Deploy methods
//




