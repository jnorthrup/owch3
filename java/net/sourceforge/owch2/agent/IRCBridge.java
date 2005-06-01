package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.AbstractAgent;
import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.kernel.MetaProperties;
import net.sourceforge.owch2.kernel.Notification;

import java.util.*;

public class IRCBridge extends AbstractAgent {
    String[] agents = {"", ""};

    public IRCBridge(Map<? extends Object, ? extends Object> m) {
        super(m);
        super.relocate();
        final String aaaa = get("IRCAgents").toString();
        setAgents(aaaa);

    }

    public void setAgents(final String agentsIn) {
        StringTokenizer st = new StringTokenizer(agentsIn);

        List<String> l = new LinkedList<String>();
        while (st.hasMoreElements()) {
            String s = (String) st.nextElement();
            l.add(s);
        }
        agents = (String[]) l.toArray(agents);
    }


    public void handle_IRC_PRIVMSG(MetaProperties m) {
        final String ircAgent = m.get("IRCAgent").toString(),
                ircNickName = m.get("JMSReplyTo").toString(),
                preliminaryValue = m.get("Value").toString(),
                finalValue = "<" + ircNickName + "@" + ircAgent + "> " + preliminaryValue;
        String agent;
        Notification repeatedMessage;

        for (int i = 0; i < agents.length; i++) {
            agent = agents[i];
            if (ircAgent.equals(agent))
                continue;
            repeatedMessage = new Notification(m);
            repeatedMessage.put("JMSType", "MSG");
            repeatedMessage.put("IRCChannel", getJMSReplyTo());
            repeatedMessage.put("JMSDestination", agent);
            repeatedMessage.put("Value", finalValue);
            if (Env.getInstance().logDebug)
                Env.getInstance().log(448, getJMSReplyTo() + ">>" + repeatedMessage.toString());
            send(repeatedMessage);
        }
    }

    ;

    public static void main(String[] args) {
        Map<? extends Object, ? extends Object> m = Env.getInstance().parseCommandLineArgs(args);
        final String[] ka = {"JMSReplyTo", "IRCAgents",};

        if (!m.keySet().containsAll(Arrays.asList(ka))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                    "IRCBridge usage:\n\n" +
                    "-name (String)name --(channel name e.g. #python)\n" +
                    "-IRCAgents (String)'agent1[ agent..n]'\n" +
                    "[-Deploy 'host1[ ..hostn]']\n" +
                    "$Id: IRCBridge.java,v 1.2 2005/06/01 06:43:11 grrrrr Exp $\n");
        }
        IRCBridge d = new IRCBridge(m);
    }
}