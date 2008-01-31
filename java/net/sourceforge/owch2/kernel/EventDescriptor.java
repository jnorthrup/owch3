package net.sourceforge.owch2.kernel;

import java.net.*;
import java.util.*;

/**
 * A EventDescriptor passed from agent to agent.
 * <p/>
 * Messages are intended to be commands, or references to resources,
 * but not to exceed reasoonable realtime traffic of a 1-2k at most,
 * excepting streaming delivery or local IPC.  no java serialization
 * is yet implied if ever...
 *
 * @author James Northrup
 * @version $Id$
 */
public class EventDescriptor extends HashMap<String, Object> {
    public static final String REPLYTO_KEY = "JMSReplyTo";
    public static final String DESTINATION_KEY = "JMSDestination";
    public static final String TYPE_KEY = "JMSType";
    public static final String MESSAGE_ID_KEY = "JMSMessageID";
    public static final String PRIORITY_KEY = "Priority";
    public static final String SOURCE_KEY = "Source";
    public static final String DEPLOY_KEY = "Deploy";
    public static final String URI_KEY = "URI";

    public EventDescriptor() {
    }

    /**
     * putall ctor
     *
     * @param message this is short succinct set
     *                of transport-specific metadata to
     *                deliver references
     *                or tiny realtime messages across
     *                an automated router
     */
    public EventDescriptor(Map<String, ?> message) {
        super(message);
    }

    public EventDescriptor(URI uri) {
        super();
        put(URI_KEY, uri);
    }

    public String getJMSReplyTo() {
        return (String) get(REPLYTO_KEY);
    }

    public URI getURI() {
        return URI.create((String) get(URI_KEY));
    }

    public String getDestination() {
        return (String) get(DESTINATION_KEY);
    }

}