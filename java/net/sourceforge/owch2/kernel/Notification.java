package net.sourceforge.owch2.kernel;

import java.util.*;

/**
 * A Message passed from agent to agent.  Contains a minimum amount of Reply and destination information.
 * @version $Id: Notification.java,v 1.1 2002/12/08 16:05:50 grrrrr Exp $
 * @author James Northrup
 */
public class Notification extends Location {
    public Notification() {
    };
 
    /** Copy C'tor */
    public Notification(Map p) {
        super(p);
    }

}

