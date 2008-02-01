package net.sourceforge.gui;

import net.sourceforge.gui.IRC.*;
import net.sourceforge.owch2.kernel.*;

import java.util.*;

public class AgentGUI {
    private AgentVisitor gui = new IRCVisitor();

    public AgentGUI() {
        super();

    }

    public static void main(String[] args) throws Exception {
        final Iterable<Map.Entry<CharSequence, Object>> iterable = Env.getInstance().parseCommandLineArgs(args);
        AgentGUI f = new AgentGUI();
        final AgentVisitor visitor = f.getGui();
        for (Map.Entry<CharSequence, Object> charSequenceObjectEntry : iterable) {
            visitor.put(charSequenceObjectEntry.getKey(), charSequenceObjectEntry.getValue());
        }
        Thread.sleep(10000);
    }

    public AgentVisitor getGui() {
        return gui;
    }

    public void setGui(AgentVisitor gui) {
        this.gui = gui;
    }
}


