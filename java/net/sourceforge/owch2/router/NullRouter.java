package net.sourceforge.owch2.router;

import java.util.*;

/**
 * this class came about by accident one day and seemed to fit..  null is all-seeing all forgiving and 100% black hole.
 *
 * @author James Northrup
 * @version $Id$
 */
public class NullRouter implements Router {
    public boolean hasPath(String key) {
        return true;
    }

    public boolean pathExists(Map<String, ?> item) {
        return true;
    }


    public void remove(String key) {
    }

    public Set getPool() {
        return new HashSet(1);
    }


    public void send(Map<String, ?> item) {
    }

    public String getDestination(Map<String, ?> item) {
        return null;
    }
}


