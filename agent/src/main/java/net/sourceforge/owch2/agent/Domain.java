package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.protocol.Transport;
import net.sourceforge.owch2.protocol.TransportEnum;

import java.beans.XMLEncoder;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Exchanger;

import static java.util.logging.Logger.getAnonymousLogger;
import static net.sourceforge.owch2.kernel.Env.getInstance;
import static net.sourceforge.owch2.protocol.TransportEnum.*;

/**
 * application level interface to a parent routing node.
 *
 * @author Jim Northrup
 */
public class Domain extends Deploy {
    public static final String DOMAIN_GATEWAY_KEY = "Domain-Gateway";
    private static final Exchanger<ByteBuffer> XML_WRITEX = new Exchanger<ByteBuffer>();

    public Domain() {
    }

    /**
     * default ctor
     */
    public Domain(Map.Entry<CharSequence, Object>... p) {
        super(p);

        Env.cmdLineHelp(
        "\n\n******************** cmdline syntax error\n" +
                            "Domain Agent usage:\n\n" +
                            "-FROM_KEY (String)name\n" +
                            "-owch:Port (int)port\n" +
                            "$Id$\n");

        //Kickstarts the protocols by requesting thier locations.

        owch.getURI();
        http.getURI();
        Env env = getInstance();
        env.setParentHost(true);

        Env.Companion.setInboundTransports(
                new Transport[]{
                        owch, local, null
                }
        );
        Env.Companion.setOutboundTransports(new Transport[]{
                local, owch, TransportEnum.Default
        });
    }

    public Domain(Iterable<Map.Entry<CharSequence, Object>> entryIterable) {
        this(entryIterable.iterator());
    }

    public Domain(Iterator<Map.Entry<CharSequence, Object>> entryIterator) {
        super(entryIterator);
    }

    public final boolean isParent() {
        return true;
    }

    public static void main(String... args) {
        Domain domain = new Domain(getInstance().parseCommandLineArgs(args));

        XMLEncoder e = new XMLEncoder(System.out);
        e.writeObject(domain);
        e.close();

        getAnonymousLogger().info("Domain Created as " + domain.toString());
    }
}
