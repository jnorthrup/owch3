package net.sourceforge.owch2.kernel;

/**
 * MetaAgent contains one or both of Name (JMSReplyTo) and URL.
 *
 * @author James Northrup
 * @version $Id: MetaAgent.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public interface MetaAgent {
    public String getJMSReplyTo();

    public String getURL();
}


