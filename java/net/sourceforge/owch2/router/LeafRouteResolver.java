package net.sourceforge.owch2.router;

import static net.sourceforge.owch2.kernel.ProtocolType.*;

import java.util.*;

/**
 * @author James Northrup
 * @version $Id: LeafRouteResolver.java,v 1.2 2005/06/03 18:27:47 grrrrr Exp $
 */
public class LeafRouteResolver extends RouteResolverImpl {
    public Collection getOutbound() {
        return outbound;
    }

    public void setOutbound(Collection<Router> outbound) {

    }

    public Collection getInbound() {
        return inbound;
    }

    public void setInbound(Collection<Router> inbound) {
    }

    private static Collection<Router> outbound;

    static {
        outbound = Arrays.asList(
                new Router[]{
                        ipc.routerInstance(),
                        owch.routerInstance(),
                        Http.routerInstance(),
                        Default.routerInstance()});
    }


    private static Collection<Router> inbound;

    static {
        inbound = Arrays.asList(
                new Router[]{ipc.routerInstance(),
                        owch.routerInstance(),
                        Http.routerInstance(),});
    }


}


