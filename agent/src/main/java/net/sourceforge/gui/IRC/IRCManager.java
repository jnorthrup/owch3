package net.sourceforge.gui.IRC;

import net.sourceforge.owch2.agent.IRC;
import net.sourceforge.owch2.kernel.ImmutableNotification;
import net.sourceforge.owch2.kernel.Notification;

import java.awt.*;
import java.util.Map;

public class IRCManager extends IRC {
    public void handle_IRC_JOIN(Notification m) {
        if (m.get(FROM_KEY).equals(get("IRCNickname"))) {
            m.put(ImmutableNotification.FROM_KEY, getFrom());
            Component cg = new IRCChannelGUI(m);
            MainFrame.desktop.add(cg);
            cg.setVisible(true);
        }
    }

    IRCVisitor MainFrame;

    public IRCManager(IRCVisitor a, Map<? extends String, ? extends Object> p) {
        super(p);
        MainFrame = a;
    }

}




