package net.sourceforge.owch2.kernel;

import java.util.*;

/**
 * A Message passed from agent to agent.  Contains a minimum amount of Reply and destination information.
 *
 * @author James Northrup
 * @version $Id: Notification.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $
 */
public class Notification extends Location {
    public static final String REPLYTO_KEY = "JMSReplyTo";
    public static final String DESTINATION_KEY = "JMSDestination";
    public static final String TYPE_KEY = "JMSType";
    public static final String SERIALNUMBER_KEY = "JMSMessageID";
    public static final String PRIORITY_KEY = "Priority";
    protected static final String SOURCE_KEY = "Source";
    protected static final String DEPLOY_KEY = "Deploy";

    public Notification() {
    }

    /**
     * Copy C'tor
     */
    public Notification(Map p) {
        super(p);
    }

}

