package net.sourceforge.owch2.protocol.router;

import net.sourceforge.owch2.kernel.EventDescriptor;
import net.sourceforge.owch2.protocol.Receipt;

import java.net.URI;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;

/**
 * this class came about by accident one day and seemed to fit..  null is all-seeing all forgiving and 100% black hole.
 *
 * @author James Northrup
 * @version $Id: NullRouter.java 10 2008-01-26 09:39:53Z grrrrr $
 */
public class NullRouter implements Router {
    public ConcurrentMap<String, URI> getPathMap() {
        return null;  //Todo: verify for a purpose
    }

    public URI getPath(EventDescriptor destination) {
        return null;  //Todo: verify for a purpose
    }

    public boolean hasPath(EventDescriptor location) {
        return false;  //Todo: verify for a purpose
    }

    public Future<Receipt> route(EventDescriptor... async) throws Exception {
        return null;  //Todo: verify for a purpose
    }

    public URI remove(String jmsReplyTo) {
        return null;  //Todo: verify for a purpose
    }
}


