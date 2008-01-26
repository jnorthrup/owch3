package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;
import static net.sourceforge.owch2.kernel.ProtocolType.*;
import net.sourceforge.owch2.router.*;

import static java.lang.Thread.*;
import java.util.*;

/**
 * application level interface to a parent routing node.
 *
 * @author Jim Northrup
 */
public class Domain extends Deploy {
    public static final String DOMAIN_GATEWAY_KEY = "Domain-Gateway";

    /**
     * default ctor
     */
    public Domain(Map p) {
        super(p);

        //Kickstarts the protocols by requesting thier locations.

        owch.getLocation();
        Http.getLocation();
        Env env = Env.getInstance();
        env.setParentHost(true);
        env.setRouteHunter(new DefaultRouteResolver());
    }

    public final boolean isParent() {
        return true;
    }

    public static void main(String[] args) throws Exception {
        Map<?, ?> m = Env.getInstance().parseCommandLineArgs(args);
        final String portString = "owch:Port";
        final String[] ka = {"JMSReplyTo", portString,};

        if (!m.keySet().containsAll(Arrays.asList(ka))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                    "Domain Agent usage:\n\n" +
                    "-JMSReplyTo (String)name\n" +
                    "-owch:Port (int)port\n" +
                    "$Id: Domain.java,v 1.3 2005/06/03 18:27:46 grrrrr Exp $\n");
        }

        Domain d = static_init(m, portString);


        static_spin(d);
    }

    private static void static_spin(AbstractAgent d) throws InterruptedException {
        while (!d.killFlag) sleep(60000);
    }


    private static Domain static_init(Map<?, ?> m, Object portString) {
        Env.getInstance().setParentHost(true);
        final String s = m.get(portString).toString();
        final int port = Integer.parseInt(s);
        Env.getInstance().setOwchPort(port);

        Domain d = new Domain(m);
        return d;
    }
}
