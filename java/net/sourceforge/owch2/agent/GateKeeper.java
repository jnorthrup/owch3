package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.AbstractAgent;
import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.kernel.MetaProperties;

import java.util.Map;

/**
 * gatekeeper registers a prefix of an URL such as "/cgi-bin/foo.cgi" The algorithm to locate the URL works in 2 phases;<OL>
 * <LI> The weakHashMap is checked for an exact match. <LI> The arraycache is then checked from top to bottom to see if
 * URL startswith (element <n>) </OL> The when an URL is located -- registering the URL "/" is a sure
 * bet, the owch agent registered in the WeakHashMap is notified of a waiting pipeline
 */
public class GateKeeper extends AbstractAgent {
    public static void main(String[] args) {
        Map m = Env.parseCommandLineArgs(args);
        if (!(m.containsKey("JMSReplyTo") && m.containsKey("HostPort"))) {
            Env.cmdLineHelp("\n\n******************** cmdline syntax error\n" + "GateKeeper Agent usage:\n\n" +
                    "-name name\n" + "-HostPort port\n" + "$Id: GateKeeper.java,v 1.1 2002/12/08 16:05:48 grrrrr Exp $\n");
        }
        ;
        GateKeeper d = new GateKeeper(m);
        Thread t = new Thread();
        try {
            t.start();
            while (true) {
                t.sleep(60000);
            }
        }
        catch (Exception e) {
        }
        ;
    };

    /**
     * @param to recipient owch node name
     * @param arg the text of the message
     */
    public void handle_Register(MetaProperties notificationIn) {
        try {
            String Item = notificationIn.get("URLSpec").toString();
            notificationIn.put("URL", notificationIn.get("URLFwd"));
            Env.gethttpRegistry().registerItem(Item, notificationIn);
            return;
        }
        catch (Exception e) {
        }
        ;
        return;
    };

    public void handle_UnRegister(MetaProperties notificationIn) {
        try {
            String Item = notificationIn.get("URLSpec").toString();
            notificationIn.put("URL", notificationIn.get("URLFwd"));
            Env.gethttpRegistry().unregisterItem(Item);
            return;
        }
        catch (Exception e) {
        }
        ;
        return;
    }

    /**
     * this has the effect of taking over the command of the http
     * service on the agent host and handling messages to marshal http registrations
     */
    public GateKeeper(Map m) {
        super(m);
        Env.getLocation("http");
    }
}

;


