package owch;

/** ****************************************************************
 * @version $Id: IPCRouter.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup
 **************************************************************** */
public class IPCRouter implements Router {

    private java.util.Map elements = new java.util.WeakHashMap();

    public void remove( Object key ) {
        Node n = ( Node )elements.get( key );
        n.handle_Dissolve( null );
        elements.remove( key );
    };

    public void send( java.util.Map item ) {
        Env.debug( 500, getClass().getName() + " sending item to" +
        getDestination( item ) );
        Node node = ( Node )elements.get( getDestination( item ) );
        node.receive(
            new Notification( item ) );
    }

    public Object getDestination( java.util.Map item ) {
        return item.get( "JMSDestination" ); //
    };

    public java.util.Set getPool() {
        return elements.keySet(); //
    };

    public Router getNextOutbound() {
        return Env.getRouter( "owch" ); //
    }

    public Router getNextInbound() { return null; };

    public boolean hasElement( Object key ) {
        return elements.containsKey( key ); //
    };

    public void put( Node node ) {
        elements.put( node.getJMSReplyTo(), node ); //
    }

    public boolean addElement( java.util.Map item ) {
        if ( item instanceof Node ) {
            //check for a previous element of same name...
            // dissolve it..
            Node n = ( Node )elements.get( "JMSReplyTo" );
            if ( n != null ) {
                n.handle_Dissolve( null );
            }
            elements.put( item.get( "JMSReplyTo" ), item );
            Env.debug( 500, getClass().getName() +
            " adding item " + item.get( "JMSReplyTo" ) );
            return true;
        }
        return false;
    };
};




