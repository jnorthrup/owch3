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
public class GateKeeper extends AbstractAgent {
    private httpRegistry httpRegistry = Env.getInstance().getHttpRegistry();

    public GateKeeper(Map<CharSequence, Object> map) {
        super(map);
    }


    //todo:  modernize the agent spinning into the workerques
    public static void main(String[] args) throws InterruptedException {

        final Map<CharSequence, Object> map = getMap(Env.getInstance().parseCommandLineArgs(args));

        if (!map.containsKey(ImmutableNotification.FROM_KEY)) {
            Env.cmdLineHelp("\n\n******************** cmdline syntax error\n" + "GateKeeper Agent usage:\n\n" +
                    "-name name\n" + "$Id$\n");
        }
        Thread t = new Thread();
        t.start();
        GateKeeper d = new GateKeeper(map);
        while (!Env.getInstance().shutdown) {
            sleep(60000);
        }

    }

    public void handle_Register(Notification notificationIn) {
        String item = (String) notificationIn.get("URLSpec");
        notificationIn.put("URL", notificationIn.get("URLFwd"));
        httpRegistry.registerItem(item, new DefaultMapNotification(notificationIn));
    }


    /**
     * this once
     *
     * @param notificationIn
     */
    public void handle_UnRegister(Notification notificationIn) {
        Object item = (String) notificationIn.get("URLSpec");
        notificationIn.put("URL", notificationIn.get("URLFwd"));
        httpRegistry.unregisterItem(item);

    }

}



