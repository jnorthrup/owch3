package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.router.DefaultRouteHunter;

import java.util.Arrays;
import java.util.Map;

/**
 * application level interface to a parent routing node.
 * @author Jim Northrup
 */
public class Domain extends Deploy {
    /** default ctor */
    public Domain(Map p) {
        super(p);
        Env.getLocation("owch");
        Env.getLocation("http");
        Env.setParentHost(true);
        Env.setRouteHunter(new DefaultRouteHunter());
    };

    public final boolean isParent() {
        //  Thread.currentThread().yield();
        return true;
    }

    public static void main(String[] args) throws Exception {
        Map m = Env.parseCommandLineArgs(args);
        final String portString = "HostPort";
        final String[] ka = {"JMSReplyTo", portString, };

        if (!m.keySet().containsAll(Arrays.asList(ka))) {
            Env.cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                            "Domain Agent usage:\n\n" +
                            "-JMSReplyTo (String)name\n" +
                            "-HostPort (int)port\n" +
                            "$Id: Domain.java,v 1.1 2002/12/08 16:05:48 grrrrr Exp $\n");
        }

        Domain d = static_init(m, portString);


        static_spin(d);
    }

    private static void static_spin(Domain d) throws InterruptedException {
        while (!d.killFlag)
            Thread.currentThread().sleep(60000);
    }


    private static Domain static_init(Map m, final String portString) {
        Env.setParentHost(true);
        final String s = m.get(portString).toString();
        final int port = Integer.parseInt(s);
        Env.setHostPort(port);

        Domain d = new Domain(m);
        return d;
    }
}
