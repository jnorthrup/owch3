package net.sourceforge.owch2.kernel;

import java.util.*;

/**
 * A Message passed from agent to agent.  Contains a minimum amount of Reply and destination information.
 *
 * @author James Northrup
 * @version $Id: Message.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class Message extends Location {
    public static final String REPLYTO_KEY = "JMSReplyTo";
    public static final String DESTINATION_KEY = "JMSDestination";
    public static final String TYPE_KEY = "JMSType";
    public static final String MESSAGE_ID_KEY = "JMSMessageID";
    public static final Object PRIORITY_KEY = "Priority";
    protected static final String SOURCE_KEY = "Source";
    protected static final Object DEPLOY_KEY = "Deploy";

    public Message() {
    }

    /**
     * Copy C'tor
     */
    public Message(Map p) {
        super(p);
    }

}

