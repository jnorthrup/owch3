package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;
import static net.sourceforge.owch2.protocol.Transport.*;

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
        http.getLocation();
        Env env = Env.getInstance();
        env.setParentHost(true);
        env.setRouteHunter(new DefaultPathResolver());
    }

    public final boolean isParent() {
        return true;
    }

    public static void main(String[] args) throws Exception {
        Map<String, ?> m = Env.getInstance().parseCommandLineArgs(args);

        List<String> stringList = Arrays.asList(Message.REPLYTO_KEY, "owch:Port");
        Collection<String> objects = new LinkedList<String>(m.keySet());
        boolean b = objects.containsAll(stringList);
        boolean empty = m.isEmpty();
        if (!empty && b) {

            Domain d = static_init(m, "owch:Port");
            static_spin(d);

        } else {
            Env.getInstance().cmdLineHelp(
                    "\n\n******************** cmdline syntax error\n" +
                            "Domain Agent usage:\n\n" +
                            "-JMSReplyTo (String)name\n" +
                            "-owch:Port (int)port\n" +
                            "$Id$\n");
        }

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
