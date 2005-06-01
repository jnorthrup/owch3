package net.sourceforge.owch2.router;

import static net.sourceforge.owch2.kernel.Env.getInstance;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author James Northrup
 * @version $Id: DefaultRouteHunter.java,v 1.1 2005/06/01 06:43:12 grrrrr Exp $
 */
public class DefaultRouteHunter extends RouteHunterImpl {
    private static Collection<Router> inbound;
    private static Collection<Router> outbound;

    static {
        outbound = Arrays.asList(new Router[]{
            getInstance().getRouter("IPC"),
            getInstance().getRouter("owch"),
            getInstance().getRouter("http"),
            getInstance().getRouter("Domain"),
            getInstance().getRouter("null")});

        inbound = Arrays.asList(new Router[]{
            getInstance().getRouter("IPC"),
            getInstance().getRouter("owch"),
            getInstance().getRouter("http")});

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