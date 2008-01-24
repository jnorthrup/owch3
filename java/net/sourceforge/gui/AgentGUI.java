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

    ;

    public static void main(String[ ] args) throws Exception {
        Map map = Env.getInstance().parseCommandLineArgs(args);
        AgentGUI f = new AgentGUI();
        Iterator i = map.keySet().iterator();
        while (i.hasNext()) {
            Object key = i.next();
            f.getGui().put(key, pair(map, key));
        }
        Thread.currentThread().sleep(10000);
    }

    private static Object pair(Map map, Object key) {
        return map.get(key);
    }

    public AgentVisitor getGui() {
        return gui;
    }

    public void setGui(AgentVisitor gui) {
        this.gui = gui;
    }
}


