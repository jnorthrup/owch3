package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.*;

import java.util.*;

/**
 * @author James Northrup
 * @version $Id: DefaultRouteHunter.java,v 1.2 2005/06/03 18:27:47 grrrrr Exp $
 */
public class DefaultRouteHunter extends RouteHunterImpl {
    private static Collection<Router> inbound;
    private static Collection<Router> outbound;


    static {
        outbound = Arrays.asList(new Router[]{
            ProtocolType.ipc.routerInstance(),
            ProtocolType.owch.routerInstance(),
            ProtocolType.Http.routerInstance(),
            ProtocolType.Domain.routerInstance(),
            ProtocolType.Null.routerInstance()});

        inbound = Arrays.asList(new Router[]{
            ProtocolType.ipc.routerInstance(),
            ProtocolType.owch.routerInstance(),
            ProtocolType.Http.routerInstance()});

    }


    public Collection<Router> getOutbound() {
        return outbound;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setOutbound(Collection<Router>  outbound) {

    }

    public Collection<Router> getInbound() {
        return inbound;
    }

    public void setInbound(Collection<Router> inbound) {

    }
}