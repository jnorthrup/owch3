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
        Map map = Env.getInstance().parseCommandLineArgs(args);
        AgentGUI f = new AgentGUI();
        for (Object o : map.keySet()) {
            f.getGui().put((String) o, map.get(o));
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


