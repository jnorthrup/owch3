package net.sourceforge.owch2.agent;

import net.sourceforge.owch2.kernel.*;

import java.util.*;

public class IRCBridge extends AbstractAgent {
    String[] agents = {"", ""};

    public IRCBridge(Map<?, ?> m) {
        super(m);
        super.relocate();
        final String aaaa = get("IRCAgents").toString();
        setAgents(aaaa);

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


    public void handle_IRC_PRIVMSG(EventDescriptor m) {
        final String ircAgent = m.get("IRCAgent").toString(),
                ircNickName = m.get(EventDescriptor.REPLYTO_KEY).toString(),
                preliminaryValue = m.get("Value").toString(),
                finalValue = "<" + ircNickName + "@" + ircAgent + "> " + preliminaryValue;
        String agent;
        EventDescriptor repeatedEventDescriptor;

        for (String agent1 : agents) {
            agent = agent1;
            if (ircAgent.equals(agent))
                continue;
            repeatedEventDescriptor = new EventDescriptor(m);
            repeatedEventDescriptor.put("JMSType", "MSG");
            repeatedEventDescriptor.put("IRCChannel", getJMSReplyTo());
            repeatedEventDescriptor.put(EventDescriptor.DESTINATION_KEY, agent);
            repeatedEventDescriptor.put("Value", finalValue);
            send(repeatedEventDescriptor);
        }
    }

    public static void main(String[] args) {
        Map m = Env.getInstance().parseCommandLineArgs(args);
        final String[] ka = {EventDescriptor.REPLYTO_KEY, "IRCAgents",};

        if (!m.keySet().containsAll(Arrays.asList(ka))) {
            Env.getInstance().cmdLineHelp("\n\n******************** cmdline syntax error\n" +
                    "IRCBridge usage:\n\n" +
                    "-name (String)name --(channel name e.g. #python)\n" +
                    "-IRCAgents (String)'agent1[ agent..n]'\n" +
                    "[-Deploy 'host1[ ..hostn]']\n" +
                    "$Id$\n");
        }
        IRCBridge d = new IRCBridge(m);
    }
}
