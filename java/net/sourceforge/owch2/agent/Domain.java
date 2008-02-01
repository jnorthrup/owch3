package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;
import net.sourceforge.owch2.protocol.*;
import static net.sourceforge.owch2.protocol.TransportEnum.*;

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
    public Domain(Iterable<Map.Entry<CharSequence, Object>> p) {
        super(p);

//        Env.cmdLineHelp(
//        "\n\n******************** cmdline syntax error\n" +
//                            "Domain Agent usage:\n\n" +
//                            "-JMSReplyTo (String)name\n" +
//                            "-owch:Port (int)port\n" +
//                            "$Id$\n");

        //Kickstarts the protocols by requesting thier locations.

        owch.getURI();
        http.getURI();
        Env env = Env.getInstance();
        env.setParentHost(true);

        Env.setInboundTransports(
                new Transport[]{
                        owch, local, TransportEnum.Null
                }
        );
        Env.setOutboundTransports(new Transport[]{
                local, owch, Null
        });
    }

    public final boolean isParent() {
        return true;
    }

    public static void main(String[] args) throws Exception {
        new Domain(Env.getInstance().parseCommandLineArgs(args));
    }

}
