package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.util.*;

/**
 * gatekeeper registers a prefix of an URL such as "/cgi-bin/foo.cgi" The algorithm to locate the URL works in 2 phases;<OL>
 * <LI> The weakHashMap is checked for an exact match. <LI> The arraycache is then checked from top to bottom to see if
 * URL startswith (element <n>) </OL> The when an URL is located -- registering the URL "/" is a sure
 * bet, the owch agent registered in the WeakHashMap is notified of a waiting pipeline
 */
public class GateKeeper extends AbstractAgent {
    public static void main(String[] args) {
        Map<? extends Object, ? extends Object> m = Env.getInstance(). parseCommandLineArgs(args);
        if (!(m.containsKey("JMSReplyTo") && m.containsKey("HostPort"))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" + "GateKeeper Agent usage:\n\n" +
                    "-name name\n" + "-HostPort port\n" + "$Id: GateKeeper.java,v 1.3 2005/06/03 18:27:47 grrrrr Exp $\n");
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
    }

    ;


    public void handle_Register(MetaProperties notificationIn) {
        try {
            String Item = notificationIn.get("URLSpec").toString();
            notificationIn.put("URL", notificationIn.get("URLFwd"));
//        if (webRegistry == null) {
//            webRegistry = new httpRegistry();
//        }
//        return webRegistry;
            httpRegistry.getInstance().registerItem(Item, notificationIn);
            return;
        }
        catch (Exception e) {
        }
        ;
        return;
    }

    ;

    public void handle_UnRegister(MetaProperties notificationIn) {
        try {
            String Item = notificationIn.get("URLSpec").toString();
            notificationIn.put("URL", notificationIn.get("URLFwd"));
//        if (webRegistry == null) {
//            webRegistry = new httpRegistry();
//        }
//        return webRegistry;
            httpRegistry.getInstance().unregisterItem(Item);
            return;
        }
        catch (Exception e) {
        }
        ;
        return;
    }

    /**
     * this has the effect of taking over the command of the Http
     * service on the agent host and handling messages to marshal Http registrations
     */
    public GateKeeper(Map<? extends Object, ? extends Object> m) {
        super(m);
        //if (Env.logDebug) Env.log(50, "Env.getLocation - " + Protocol);

        MetaProperties l = ProtocolType.Http.getLocation();

    }
}

;


