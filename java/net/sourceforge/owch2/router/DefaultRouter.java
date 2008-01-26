package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;
import static net.sourceforge.owch2.kernel.Env.*;
import static net.sourceforge.owch2.kernel.Message.*;

import java.util.*;

/**
 * @author James Northrup
 * @version $Id$
 */
public class DefaultRouter implements Router {
    public Set getPool() {
        return new TreeSet(); //
    }

    public void send(Map<String, ?> item) {
        boolean hasParent = getInstance().getParentNode() != null;
        if (!hasParent) {
            return;
        }
        Router router = ProtocolType.owch.routerInstance();


        ((Map) item).put(URI_KEY, Env.getInstance().getDefaultURI());
        router.send(item);
    }

    public void remove(String key) { //
    }


    public String getDestination(Map<String, ?> item) {
        return null;
    }

    public boolean pathExists(Map<String, ?> item) {
        return false;
    }

    public boolean hasPath(String key) {
        return null != getInstance().getParentNode(); //
    }
}
