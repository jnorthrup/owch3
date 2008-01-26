package net.sourceforge.gui.IRC;

import net.sourceforge.owch2.agent.*;
import net.sourceforge.owch2.kernel.*;

import java.awt.*;
import java.util.*;

public class IRCManager extends IRC {
    public void handle_IRC_JOIN(MetaProperties m) {

        if (m.get("JMSReplyTo").toString().equals(get("IRCNickname").toString())) {
            m.put("JMSReplyTo", getJMSReplyTo());
            Component cg = new IRCChannelGUI(m);
            MainFrame.desktop.add(cg);
            cg.setVisible(true);
        }
    }

    IRCVisitor MainFrame;

    public IRCManager(IRCVisitor a, Map p) {
        super(p);
        MainFrame = a;
    }

}




