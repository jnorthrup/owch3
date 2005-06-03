package net.sourceforge.owch2.router;

import java.util.*;

/**
 * @author James Northrup
 * @version $Id: Router.java,v 1.2 2005/06/03 18:27:47 grrrrr Exp $
 */
public interface Router {
    public void remove(Object key);

    Set getPool();

    void send(Map item);

    Object getDestination(Map item);

    boolean proxyAccepted(Map item);

    boolean hasElement(Object key);
}


