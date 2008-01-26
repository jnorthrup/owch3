package net.sourceforge.owch2.kernel;

/**
 * MetaAgent contains one or both of Name (JMSReplyTo) and URL.
 * <p/>
 * Every Message that is intended for roundtrip also contains
 * the META tags to reach the source agent.  Thus any and all
 * messages are also proxies to thier origin via the previous hop at least.
 * <p/>
 * this is how routers and domain gateways interchange various sources without
 * a preconfigured point of failure or protocol assumption.  each hop is assumed
 * to handle agent info as opaque addresses
 *
 * @author James Northrup
 * @version $Id: MetaAgent.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public interface MetaAgent {
    public String getJMSReplyTo();

    public String getURI();
}


