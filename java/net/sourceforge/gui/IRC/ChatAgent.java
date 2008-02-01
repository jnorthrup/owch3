package net.sourceforge.gui.IRC;

import net.sourceforge.gui.*;
import net.sourceforge.nlp.*;
import net.sourceforge.owch2.kernel.*;

import java.util.*;

public class ChatAgent extends AbstractAgent {
    ChatGUI gui;
    private SentenceParser sParser;

    public ChatAgent(ChatGUI g, HasProperties l) {
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
    public void handle_Dissolve(HasProperties n1) {
        final DefaultMapTransaction n = new DefaultMapTransaction(this);
        n.put(ImmutableNotification.DESTINATION_KEY, get("IRCManager"));
        n.put(TYPE_KEY, "PART");
        n.put("Value", getFrom());
        n.put(ImmutableNotification.FROM_KEY, getFrom());
        send(n);
        sParser.write(getFrom() + ".hist");
        super.handle_Dissolve(new DefaultMapTransaction(this));
    }

    public void handle_IRC_PRIVMSG(Notification m) {
        String value = m.get("Value").toString();
        ScrollingListModel lm = (ScrollingListModel) gui.getMsgList().getModel();
        List<Report> l = sParser.tokenize(value);
        Iterator<Report> i = l.iterator();
        while (i.hasNext()) lm.addElement(i.next());
        while (lm.getSize() > 1000) {
            lm.remove(0);
        }


    }

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
    }

    public void handle_IRC_RPL_ENDOFNAMES(Notification m) {
        gui.getUsersList().invalidate();
        gui.getUsersList().repaint();
    }

}




