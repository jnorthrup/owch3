package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import static java.lang.Thread.*;
import java.util.*;

/**
 * gatekeeper registers a prefix of an URL such as "/cgi-bin/foo.cgi" The algorithm to locate the URL works in 2 phases;<OL>
 * <LI> The weakHashMap is checked for an exact match. <LI> The arraycache is then checked from top to bottom to see if
 * URL startswith (element <n>) </OL> The when an URL is located -- registering the URL "/" is a sure
 * bet, the owch agent registered in the WeakHashMap is notified of a waiting pipeline
 */
public class GateKeeper extends AbstractAgent<String> {
    private HttpRegistry httpRegistry = Env.getInstance().getHttpRegistry();


    //todo:  modernize the agent spinning into the workerques
    public static void main(String[] args) throws InterruptedException {

        Map m = Env.getInstance().parseCommandLineArgs(args);

        if (!m.containsKey(Message.REPLYTO_KEY)) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" + "GateKeeper Agent usage:\n\n" +
                    "-name name\n" + "$Id$\n");
        }
        Thread t = new Thread();
        t.start();
        GateKeeper d = new GateKeeper(m);
        while (!Env.getInstance().shutdown) {
            sleep(60000);
        }

    }

    public void handle_Register(MetaProperties notificationIn) {
        String item = (String) notificationIn.get("URLSpec");
        notificationIn.put("URL", notificationIn.get("URLFwd"));
        httpRegistry.registerItem(item, new Location(notificationIn));
    }


    /**
     * this once
     *
     * @param notificationIn
     */
    public void handle_UnRegister(MetaProperties notificationIn) {
        Object item = (String) notificationIn.get("URLSpec");
        notificationIn.put("URL", notificationIn.get("URLFwd"));
        httpRegistry.unregisterItem(item);

    }

    /**
     * this has the effect of taking over the command of the Http
     * service on the agent host and handling messages to marshal Http registrations
     *
     * @param params bootStrap stuff
     */
    public GateKeeper(Map<String, String> params) {
        super(params);
    }
}



