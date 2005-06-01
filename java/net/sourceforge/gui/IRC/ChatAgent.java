package net.sourceforge.gui.IRC;

import net.sourceforge.gui.ScrollingListModel;
import net.sourceforge.nlp.Report;
import net.sourceforge.nlp.SentenceParser;
import net.sourceforge.nlp.Serializer;
import net.sourceforge.owch2.kernel.AbstractAgent;
import net.sourceforge.owch2.kernel.Env;
import net.sourceforge.owch2.kernel.Location;
import net.sourceforge.owch2.kernel.MetaProperties;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class ChatAgent extends AbstractAgent {
    ChatGUI gui;
    private SentenceParser sParser;

    public ChatAgent(ChatGUI g, Location l) {
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
            ;
        }
        gui = g;
    }

    /**
     * this tells our (potentially clone) agent to stop re-registering.  it will cease to spin.
     */
    public void handle_Dissolve(MetaProperties n) {
        n.put("JMSDestination", get("IRCManager"));
        n.put("JMSType", "PART");
        n.put("Value", getJMSReplyTo());
        n.put("JMSReplyTo", getJMSReplyTo());
        send(n);
        sParser.write(getJMSReplyTo() + ".hist");
        super.handle_Dissolve(new Location(this));
    }

    ;

    public void handle_IRC_PRIVMSG(MetaProperties m) {
        String value = m.get("Value").toString();
        ScrollingListModel lm = (ScrollingListModel) gui.getMsgList().getModel();
        List<Report> l = sParser.tokenize(value);
        Iterator<Report> i = l.iterator();
        while (i.hasNext()) lm.addElement(i.next());
        while (lm.getSize() > 1000) {
            lm.remove(0);
        }
        ;


    }

    ;

    public void handle_IRC_PRIVMSG2(MetaProperties m) {
        String value = m.get("Value").toString();
        ScrollingListModel lm = (ScrollingListModel) gui.getMsgList().getModel();
        String ret = (value.startsWith("\00ACTION") ? ("* " + m.get("JMSReplyTo") + " " + value.substring(6).trim()) : ("<" + m.get("JMSReplyTo") + "> " + value));
        lm.addElement(ret);
        while (lm.getSize() > 1000) {
            lm.remove(0);
        }
        sParser.tokenize(value);
    }

    ;

    public void handle_IRC_RPL_NAMREPLY(MetaProperties m) {
        StringTokenizer tk = new StringTokenizer(m.get("Value").toString());
        ScrollingListModel lm = (ScrollingListModel) gui.getUsersList().getModel();

        while (tk.hasMoreTokens()) {
            lm.addElement(tk.nextToken()); //
        }
        ;
        if (Env.getInstance().logDebug) Env.getInstance().log(50, "recvd names");
    }

    ;

    public void handle_IRC_RPL_ENDOFNAMES(MetaProperties m) {
        gui.getUsersList().invalidate();
        gui.getUsersList().repaint();
    }

    ;
}



