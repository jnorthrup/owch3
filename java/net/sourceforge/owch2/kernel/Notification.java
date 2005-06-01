package net.sourceforge.owch2.kernel;

import java.util.Map;

/**
 * A Message passed from agent to agent.  Contains a minimum amount of Reply and destination information.
 *
 * @author James Northrup
 * @version $Id: Notification.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $
 */
public class Notification extends Location {
    public Notification() {
    }

    ;

    /**
     * Copy C'tor
     */
    public Notification(Map p) {
        super(p);
    }

}

