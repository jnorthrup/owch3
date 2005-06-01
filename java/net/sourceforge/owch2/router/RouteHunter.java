
package net.sourceforge.owch2.router;

import java.util.Collection;
import java.util.Map;

public interface RouteHunter {
    void remove(Object key);

    void send(Map item);

    Collection<Router> getOutbound();

    void setOutbound(Collection<Router>  outbound);

    Collection<Router> getInbound();

    void setInbound(Collection <Router> inbound);
}


