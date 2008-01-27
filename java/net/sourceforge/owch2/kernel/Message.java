package net.sourceforge.owch2.kernel;

import java.util.*;

/**
 * A Message passed from agent to agent.  Contains a minimum amount of Reply and destination information.
 * <p/>
 * the default format is rfc822 which has lots of reuse potential
 *
 * @author James Northrup
 * @version $Id$
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
     * putall ctor
     */
    public Message(Map rfc822) {
        super(rfc822);
    }
}

