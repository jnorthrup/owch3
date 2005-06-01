package net.sourceforge.owch2.router;

import net.sourceforge.owch2.kernel.Env;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author James Northrup
 * @version $Id: LeafRouteHunter.java,v 1.1 2005/06/01 06:43:12 grrrrr Exp $
 */
public class LeafRouteHunter extends RouteHunterImpl {
    public Collection getOutbound() {
        return outbound;
    }

    public void setOutbound(Collection<Router> outbound) {
        this.outbound = outbound;
    }

    public Collection getInbound() {
        return inbound;
    }

    public void setInbound(Collection<Router> inbound) {
        this.inbound = inbound;
    }

    private static Collection<Router> outbound = Arrays.asList(
            new Router[]{Env.getInstance().getRouter("IPC"), Env.getInstance().getRouter("owch"),
                Env.getInstance().getRouter("http"), Env.getInstance().getRouter("default")});
    private static Collection<Router> inbound = Arrays.asList(
            new Router[]{Env.getInstance().getRouter("IPC"), Env.getInstance().getRouter("owch"),
                Env.getInstance().getRouter("http"),});
}

;


