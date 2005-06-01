package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.router.DefaultRouteHunter;

import java.util.Arrays;
import java.util.Map;

/**
 * application level interface to a parent routing node.
 *
 * @author Jim Northrup
 */
public class Domain extends Deploy {
    /**
     * default ctor
     */
    public Domain(Map p) {
        super(p);
        Env.getInstance().getLocation("owch");
        Env.getInstance().getLocation("http");
        Env.getInstance().setParentHost(true);
        Env.getInstance().setRouteHunter(new DefaultRouteHunter());
    }

    ;

    public final boolean isParent() {
        //  Thread.currentThread().yield();
        return true;
    }

    public static void main(String[] args) throws Exception {
        Map<? extends Object, ? extends Object> m = Env.getInstance().parseCommandLineArgs(args);
        final String portString = "HostPort";
        final String[] ka = {"JMSReplyTo", portString,};

        if (!m.keySet().containsAll(Arrays.asList(ka))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                    "Domain Agent usage:\n\n" +
                    "-JMSReplyTo (String)name\n" +
                    "-HostPort (int)port\n" +
                    "$Id: Domain.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $\n");
        }

        Domain d = static_init(m, portString);


        static_spin(d);
    }

    private static void static_spin(Domain d) throws InterruptedException {
        while (!d.killFlag) Thread.currentThread().sleep(60000);
    }


    private static Domain static_init(Map<? extends Object, ? extends Object> m, final String portString) {
        Env.getInstance().setParentHost(true);
        final String s = m.get(portString).toString();
        final int port = Integer.parseInt(s);
        Env.getInstance().setHostPort(port);

        Domain d = new Domain(m);
        return d;
    }
}
