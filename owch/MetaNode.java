package owch;
/**
 * MetaNode contains one or both of Name (JMSReplyTo) and URL.  
 */
public interface MetaNode
    {
        public String getJMSReplyTo();
        public String getURL();
    }

