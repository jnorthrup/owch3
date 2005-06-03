package net.sourceforge.gui.IRC;

import net.sourceforge.gui.*;
import net.sourceforge.owch2.kernel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.logging.*;

public class IRCVisitor extends JFrame implements AgentVisitor {
    public TextPanel IRCHostText = new TextPanel("IRCHostname")
    ,
    IRCPortText = new TextPanel("IRCPort")
    ,
    IRCJoinText = new TextPanel("Channels")
    ,
    JMSReplyToText = new TextPanel("JMSReplyTo")
    ,
    IRCNicknameText = new TextPanel("IRCNickname")
    ,
    ParentURLText = new TextPanel("ParentURL");

    private JToolBar agentToolbar = new JToolBar()
    ,
    channelBar = new JToolBar();
    private JCheckBox connectCheck = new JCheckBox();

    JDesktopPane desktop = new JDesktopPane();

    private JInternalFrame AgentDescriptor = new JInternalFrame();
    private JTabbedPane agentTabs = new JTabbedPane();
    private JPanel IRCPanel = new JPanel()
    ,
    owchPanel = new JPanel();

    private AbstractAgent node;

    public IRCVisitor() {
        initGUI();
    }

    ;

    public void initGUI() {
        ParentURLText.setColumns(24);
        ParentURLText.setText("owch://localhost:2112");
        JMSReplyToText.setText("irc");
        JMSReplyToText.setColumns(12);
        IRCNicknameText.setText("dot");
        IRCNicknameText.setColumns(14);
        IRCPortText.setText("6667");
        IRCPortText.setColumns(7);
        IRCHostText.setText("irc.openprojects.net");
        IRCHostText.setColumns(25);
        IRCJoinText.setText("owch2 debian rdf-bot");
        IRCJoinText.setColumns(24);

        owchPanel.setLayout(new BoxLayout(owchPanel, BoxLayout.Y_AXIS));
        owchPanel.add(JMSReplyToText);
        owchPanel.add(ParentURLText);
        IRCPanel.setLayout(new BoxLayout(IRCPanel, BoxLayout.Y_AXIS));
        IRCPanel.add(IRCHostText);
        IRCPanel.add(IRCPortText);
        IRCPanel.add(IRCNicknameText);
        IRCPanel.add(IRCJoinText);
        AgentDescriptor.setBounds(new Rectangle(15, 24, 399, 321));
        AgentDescriptor.setVisible(true);
        AgentDescriptor.setTitle("AgentDescriptor");
        AgentDescriptor.setResizable(true);
        AgentDescriptor.setMaximizable(true);
        AgentDescriptor.setIconifiable(true);
        // AgentDescriptor.setBorder(BorderFactory.createMatteBorder(1, 2, 1, 2, new Color(0, 0, 0)));
        //desktop.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        desktop.add(AgentDescriptor);
        connectCheck.setText("Connect");
        connectCheck.setVerticalAlignment(SwingConstants.CENTER);
        connectCheck.setHorizontalTextPosition(SwingConstants.LEADING);

        agentToolbar.add(connectCheck);
        connectCheck.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        connectCheckActionPerformed(e);
                    }
                });
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(desktop, BorderLayout.CENTER);
        getContentPane().add(channelBar, BorderLayout.SOUTH);
        getContentPane().add(agentToolbar, BorderLayout.NORTH);
        AgentDescriptor.getContentPane().add(agentTabs, BorderLayout.CENTER);
        setBounds(new Rectangle(0, 0, 694, 494));
        setTitle("owch2 IRC Agent UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        agentTabs.setToolTipText("");
        agentTabs.setTabPlacement(JTabbedPane.TOP);
        agentTabs.setBorder(BorderFactory.createEtchedBorder());
        agentTabs.setVerifyInputWhenFocusTarget(true);
        agentTabs.add(IRCPanel, "IRC");
        agentTabs.add(owchPanel, "owch2 Agent");
        setVisible(true);
    }

    ;

    public String[] getApp_keys() {
        return app_keys;
    }

    public String getApp_keys(int index) {
        return app_keys[index];
    }

    static final private String[] app_keys = {
        "IRCHost",
        "IRCPort",
        "IRCNickname",
        "IRCJoin",
        "JMSReplyTo",
        "ParentURL",
    };

    public void connectCheckActionPerformed(ActionEvent e) {
        final boolean flag = ((JCheckBox) e.getSource()).isSelected();
        if (flag) {
            startAgent();
        } else {
            stopAgent();
        }
    }

    public AbstractAgent getNode() {
        return node;
    }

    public void setNode(AbstractAgent node) {
        this.node = node;
    }

    public void startAgent() {
        Location l = new Location();
        //        AgentDescriptor.setVisible(false);
        //desktop.remove(AgentDescriptor);
        for (int i = 0; i < getApp_keys().length; i++) {
            String s = (String) get(getApp_keys(i));
            if (s != null) {
                s = s.trim();
            }
            if (s.length() > 0) {
                l.put(getApp_keys(i), s);
            }
        }
        ;
        AgentDescriptor.setEnabled(false);
        if (Env.getInstance().logDebug) Logger.global.info(l.toString());
        setNode(new IRCManager(this, l));
    }

    ;

    public Object get(String key) {

        try {
            String key1 = key.toString();
            Class<? extends Object> c = getClass();
            if (Env.getInstance().logDebug) Logger.global.info("get::" + key1 + "Text");
            Field f = c.getField(key1 + "Text");
            if (Env.getInstance().logDebug) Logger.global.info("getf::" + f.toString());
            Object o = f.get(this);
            Method m = o.getClass().getMethod("getText", AgentVisitor.no_class);
            if (Env.getInstance().logDebug) Logger.global.info("getm::" + m.toString());
            return m.invoke(o, AgentVisitor.no_Parm).toString();

        }
        catch (NoSuchFieldException e) {
            return getNode().get(key);
        }
        catch (SecurityException e) {
            return getNode().get(key);
        }
        catch (IllegalArgumentException e) {
            return getNode().get(key);
        }
        catch (IllegalAccessException e) {
            return getNode().get(key);
        }
        catch (NoSuchMethodException e) {
            return getNode().get(key);
        }
        catch (InvocationTargetException e) {
            return getNode().get(key);
        }

        //  return getNode().get(key);

    }

    public void stopAgent() {
    }

    ;

    public void put(Object key, Object val) {


        try {
            String key1 = key.toString();
            Class<? extends Object> c = getClass();
            if (Env.getInstance().logDebug) Logger.global.info("get::" + key1 + "Text");
            Field f = c.getField(key1 + "Text");
            if (Env.getInstance().logDebug) Logger.global.info("get::" + f.toString());
            Object o = f.get(this);
            Method m = o.getClass().getMethod("setText",
                    new Class[]{String.class});
            m.invoke(o,
                    new Object[]{val});
        }
        catch (NoSuchFieldException e) {
        }
        catch (SecurityException e) {
        }
        catch (IllegalArgumentException e) {
        }
        catch (IllegalAccessException e) {
        }
        catch (NoSuchMethodException e) {
        }
        catch (InvocationTargetException e) {
        }
        finally {
            getNode().put(key, val);
        }

    }
}




