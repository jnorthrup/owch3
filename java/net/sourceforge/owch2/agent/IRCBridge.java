package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.util.*;

public class IRCBridge extends AbstractAgent {
    String[] agents = {"", ""};

    public IRCBridge(Iterable<Map.Entry<CharSequence, Object>> m) {
        super(m);

        final String[] ka = {ImmutableNotification.FROM_KEY, "IRCAgents",};

        if (!keySet().containsAll(Arrays.asList(ka))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                    "IRCBridge usage:\n\n" +
                    "-name (String)name --(channel name e.g. #python)\n" +
                    "-IRCAgents (String)'agent1[ agent..n]'\n" +
                    "[-Deploy 'host1[ ..hostn]']\n" +
                    "$Id$\n");
        } else {

            super.relocate();
            final String aaaa = get("IRCAgents").toString();
            setAgents(aaaa);
        }
    }

    public void setAgents(String agentsIn) {
        Enumeration st = new StringTokenizer(agentsIn);

        Collection<String> l = new LinkedList<String>();
        while (st.hasMoreElements()) {
            String s = (String) st.nextElement();
            l.add(s);
        }
        agents = (String[]) l.toArray(agents);
    }


    public void handle_IRC_PRIVMSG(Notification m) {
        final String ircAgent = m.get("IRCAgent").toString(),
                ircNickName = m.get(ImmutableNotification.FROM_KEY).toString(),
                preliminaryValue = m.get("Value").toString(),
                finalValue = "<" + ircNickName + "@" + ircAgent + "> " + preliminaryValue;
        String agent;
        Notification repeatedNotification;

        for (String agent1 : agents) {
            agent = agent1;
            if (ircAgent.equals(agent))
                continue;
            repeatedNotification = new DefaultMapTransaction(m);
            repeatedNotification.put("JMSType", "MSG");
            repeatedNotification.put("IRCChannel", getFrom());
            repeatedNotification.put(ImmutableNotification.DESTINATION_KEY, agent);
            repeatedNotification.put("Value", finalValue);
            send((Transaction) repeatedNotification);
        }
    }

    public static void main(String[] args) {
        IRCBridge d = new IRCBridge(Env.getInstance().parseCommandLineArgs(args));
    }
}
