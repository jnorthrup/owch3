package owch;
/**
 * MetaNode contains one or both of Name (JMSReplyTo) and URL.  
 * @version $Id: MetaNode.java,v 1.2 2001/09/23 10:20:10 grrrrr Exp $
 * @author James Northrup
 */
public interface MetaNode
    {
        public String getJMSReplyTo();
        public String getURL();
    }

