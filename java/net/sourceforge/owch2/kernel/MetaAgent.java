package net.sourceforge.owch2.kernel;

/**
 * MetaAgent contains one or both of Name (JMSReplyTo) and URL.
 * @version $Id: MetaAgent.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public interface MetaAgent {
    public String getJMSReplyTo();

    public String getURL();
}


