package net.sourceforge.gui.IRC;

import net.sourceforge.gui.*;
import net.sourceforge.nlp.*;
import net.sourceforge.owch2.kernel.*;

import java.util.*;

public class IRCChannelAgent extends AbstractAgent {
    IRCChannelGUI gui;
    private SentenceParser sParser;

    /**
     * sets up nlp stuff
     */
    public IRCChannelAgent(IRCChannelGUI g, Map l) {
        super(l);
        try {
            sParser = new SentenceParser(getFrom() + ".hist");
        }
        catch (Exception e) {
            try {
                Serializer ser = new Serializer();
                ser.serialize(getFrom() + ".hist");
                sParser = new SentenceParser(getFrom() + ".hist");
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        gui = g;
    }

    /**
     * this tells our (potentially clone) agent to stop re-registering.  it will cease to spin.
     */
    public void handle_Dissolve(Notification n1) {
        final DefaultMapTransaction n = new DefaultMapTransaction(n1);
        n.put(ImmutableNotification.DESTINATION_KEY, get("IRCManager"));
        n.put("JMSType", "PART");
        n.put("Value", getFrom());
        n.put(ImmutableNotification.FROM_KEY, getFrom());
        send(n);
        sParser.write(getFrom() + ".hist");
        super.handle_Dissolve(new DefaultMapNotification(this));
    }


    /**
     * THe nlp version of handlePrivMsg
     */
    public void handle_IRC_PRIVMSG(Notification m) {
        String value = m.get("Value").toString();
        ScrollingListModel lm = (ScrollingListModel) gui.getMsgList().getModel();
        List<Report> l = sParser.tokenize(value);
        for (Report aL : l) lm.addElement(aL);
        while (lm.getSize() > 1000) {
            lm.remove(0);
        }
    }

    /**
     * THe almost-chat version of handlePrivMsg
     */
    public void handle_IRC_PRIVMSG2(Notification m) {
        String value = m.get("Value").toString();
        ScrollingListModel lm = (ScrollingListModel) gui.getMsgList().getModel();
        String ret = value.startsWith("\00ACTION") ? "* " + m.get(ImmutableNotification.FROM_KEY) + " " + value.substring(6).trim() : "<" + m.get(ImmutableNotification.FROM_KEY) + "> " + value;
        lm.addElement(ret);
        while (lm.getSize() > 1000) {
            lm.remove(0);
        }
        sParser.tokenize(value);
    }

    public void handle_IRC_RPL_NAMREPLY(Notification m) {
        StringTokenizer tk = new StringTokenizer(m.get("Value").toString());
        ScrollingListModel lm = (ScrollingListModel) gui.getUsersList().getModel();

        while (tk.hasMoreTokens()) {
            lm.addElement(tk.nextToken()); //
        }
//        if (Env.logDebug) Env.log(50, "recvd names");
    }

    public void handle_IRC_RPL_ENDOFNAMES(Notification m) {
        gui.getUsersList().invalidate();
        gui.getUsersList().repaint();
    }

}




