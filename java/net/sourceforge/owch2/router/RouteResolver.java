package net.sourceforge.owch2.router;

import java.util.*;

public interface RouteResolver {
    void remove(String key);

    void send(Map<String, ?> item);

    Collection<Router> getOutbound();

    void setOutbound(Collection<Router> outbound);

    Collection<Router> getInbound();

    void setInbound(Collection<Router> inbound);
}


