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
            sParser = new SentenceParser(getJMSReplyTo() + ".hist");
        }
        catch (Exception e) {
            try {
                Serializer ser = new Serializer();
                ser.serialize(getJMSReplyTo() + ".hist");
                sParser = new SentenceParser(getJMSReplyTo() + ".hist");
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
    public void handle_Dissolve(EventDescriptor n) {
        n.put(EventDescriptor.DESTINATION_KEY, get("IRCManager"));
        n.put("JMSType", "PART");
        n.put("Value", getJMSReplyTo());
        n.put(EventDescriptor.REPLYTO_KEY, getJMSReplyTo());
        send(n);
        sParser.write(getJMSReplyTo() + ".hist");
        super.handle_Dissolve(new EventDescriptor(this));
    }


    /**
     * THe nlp version of handlePrivMsg
     */
    public void handle_IRC_PRIVMSG(EventDescriptor m) {
        String value = m.get("Value").toString();
        ScrollingListModel lm = (ScrollingListModel) gui.getMsgList().getModel();
        List<Report> l = sParser.tokenize(value);
        Iterator<Report> i = l.iterator();
        while (i.hasNext()) lm.addElement(i.next());
        while (lm.getSize() > 1000) {
            lm.remove(0);
        }
    }

    /**
     * THe almost-chat version of handlePrivMsg
     */
    public void handle_IRC_PRIVMSG2(EventDescriptor m) {
        String value = m.get("Value").toString();
        ScrollingListModel lm = (ScrollingListModel) gui.getMsgList().getModel();
        String ret = value.startsWith("\00ACTION") ? "* " + m.get(EventDescriptor.REPLYTO_KEY) + " " + value.substring(6).trim() : "<" + m.get(EventDescriptor.REPLYTO_KEY) + "> " + value;
        lm.addElement(ret);
        while (lm.getSize() > 1000) {
            lm.remove(0);
        }
        sParser.tokenize(value);
    }

    public void handle_IRC_RPL_NAMREPLY(EventDescriptor m) {
        StringTokenizer tk = new StringTokenizer(m.get("Value").toString());
        ScrollingListModel lm = (ScrollingListModel) gui.getUsersList().getModel();

        while (tk.hasMoreTokens()) {
            lm.addElement(tk.nextToken()); //
        }
//        if (Env.logDebug) Env.log(50, "recvd names");
    }

    public void handle_IRC_RPL_ENDOFNAMES(EventDescriptor m) {
        gui.getUsersList().invalidate();
        gui.getUsersList().repaint();
    }

}




