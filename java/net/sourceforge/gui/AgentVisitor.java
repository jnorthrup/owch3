package net.sourceforge.gui;

import net.sourceforge.owch2.kernel.*;

public interface AgentVisitor {
    Object get(Object key);

    void put(Object key, Object val);

    void stopAgent();

    AbstractAgent getNode();

    void startAgent();

    void initGUI();

    /**
     * gets keys
     * @return keys
     */
    String[] getApp_keys();

    String getApp_keys(int index);

    static final String default_val = "default".intern();
    static final Object[] no_Parm = new Object[0];
    static final Class[] no_class = new Class[0];
}


