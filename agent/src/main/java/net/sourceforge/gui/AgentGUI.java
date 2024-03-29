package net.sourceforge.gui;

import net.sourceforge.gui.IRC.IRCVisitor;
import net.sourceforge.owch2.kernel.Env;

import java.util.Iterator;
import java.util.Map;

public class AgentGUI {
    private AgentVisitor gui = new IRCVisitor();

    public AgentGUI() {
        super();

    }

    public static void main(String[] args) throws Exception {
        final Iterator<Map.Entry<CharSequence, Object>> iterable = Env.getInstance().parseCommandLineArgs(args);
        AgentGUI f = new AgentGUI();
        final AgentVisitor visitor = f.getGui();

        while (iterable.hasNext()) {
            Map.Entry<CharSequence, Object> charSequenceObjectEntry = iterable.next();
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


