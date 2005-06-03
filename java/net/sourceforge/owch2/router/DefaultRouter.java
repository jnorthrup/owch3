package net.sourceforge.owch2.router;

import static net.sourceforge.owch2.kernel.Env.*;
import net.sourceforge.owch2.kernel.*;

import java.util.*;
import java.util.logging.*;

/**
 * @author James Northrup
 * @version $Id: DefaultRouter.java,v 1.1 2005/06/03 18:27:47 grrrrr Exp $
 */
public class DefaultRouter implements Router {
    public Set getPool() {
        return new TreeSet(); //
    }

    public void send(Map item) {
        boolean hasParent = getInstance().getParentNode() != null;
        if (!hasParent) {
            if (getInstance().logDebug) Logger.global.info("dropping item" + item.toString());
            return;
        }
        Router router = ProtocolType .owch.routerInstance();
        item.put(Notification.URI_KEY, getInstance().getDefaultURI());
        router.send(item);
    }

    public void remove(Object key) { //
    }


    public Object getDestination(Map item) {
        return null;
    }

    public boolean proxyAccepted(Map item) {
        return false;
    }

    public boolean hasElement(Object key) {
        boolean sated = getInstance().getParentNode() != null;
        return sated; //
    }
}

;


