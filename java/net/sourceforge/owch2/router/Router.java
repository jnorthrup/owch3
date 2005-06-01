package net.sourceforge.owch2.router;

import java.util.Map;
import java.util.Set;

/**
 * @author James Northrup
 * @version $Id: Router.java,v 1.1 2005/06/01 06:43:12 grrrrr Exp $
 */
public interface Router {
    public void remove(Object key);

    Set getPool();

    void send(Map item);

    Object getDestination(Map item);

    boolean addElement(Map item);

    ;

    boolean hasElement(Object key);

    ;
}


