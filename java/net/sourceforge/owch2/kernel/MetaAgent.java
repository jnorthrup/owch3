package net.sourceforge.owch2.kernel;

import java.net.*;

/**
 * MetaAgent contains one or both of Name (JMSReplyTo) and URL.
 *
 * @author James Northrup
 * @version $Id: MetaAgent.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public interface MetaAgent {
    public String getJMSReplyTo();

    public URI getURI();
}


