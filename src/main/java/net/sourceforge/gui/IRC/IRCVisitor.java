/*     */ package net.sourceforge.gui.IRC;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.BoxLayout;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JDesktopPane;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JInternalFrame;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTabbedPane;
/*     */ import javax.swing.JToolBar;
/*     */ import net.sourceforge.gui.AgentVisitor;
/*     */ import net.sourceforge.gui.TextPanel;
/*     */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*     */ import net.sourceforge.owch2.kernel.Env;
/*     */ import net.sourceforge.owch2.kernel.Location;
/*     */ 
/*     */ public class IRCVisitor extends JFrame
/*     */   implements AgentVisitor
/*     */ {
/*  12 */   public TextPanel IRCHostText = new TextPanel("IRCHostname");
/*  13 */   public TextPanel IRCPortText = new TextPanel("IRCPort");
/*  14 */   public TextPanel IRCJoinText = new TextPanel("Channels");
/*  15 */   public TextPanel JMSReplyToText = new TextPanel("JMSReplyTo");
/*  16 */   public TextPanel IRCNicknameText = new TextPanel("IRCNickname");
/*  17 */   public TextPanel ParentURLText = new TextPanel("ParentURL");
/*     */ 
/*  19 */   private JToolBar agentToolbar = new JToolBar();
/*  20 */   private JToolBar channelBar = new JToolBar();
/*  21 */   private JCheckBox connectCheck = new JCheckBox();
/*     */ 
/*  23 */   JDesktopPane desktop = new JDesktopPane();
/*     */ 
/*  25 */   private JInternalFrame AgentDescriptor = new JInternalFrame();
/*  26 */   private JTabbedPane agentTabs = new JTabbedPane();
/*  27 */   private JPanel IRCPanel = new JPanel(); private JPanel owchPanel = new JPanel();
/*     */   private AbstractAgent node;
/* 101 */   private static final String[] app_keys = { "IRCHost", "IRCPort", "IRCNickname", "IRCJoin", "JMSReplyTo", "ParentURL" };
/*     */ 
/*     */   public IRCVisitor()
/*     */   {
/*  32 */     initGUI();
/*     */   }
/*     */   public void initGUI() {
/*  35 */     ParentURLText.setColumns(24);
/*  36 */     ParentURLText.setText("owch://localhost:2112");
/*  37 */     JMSReplyToText.setText("irc");
/*  38 */     JMSReplyToText.setColumns(12);
/*  39 */     IRCNicknameText.setText("dot");
/*  40 */     IRCNicknameText.setColumns(14);
/*  41 */     IRCPortText.setText("6667");
/*  42 */     IRCPortText.setColumns(7);
/*  43 */     IRCHostText.setText("irc.openprojects.net");
/*  44 */     IRCHostText.setColumns(25);
/*  45 */     IRCJoinText.setText("owch2 debian rdf-bot");
/*  46 */     IRCJoinText.setColumns(24);
/*     */ 
/*  48 */     owchPanel.setLayout(new BoxLayout(owchPanel, 1));
/*  49 */     owchPanel.add(JMSReplyToText);
/*  50 */     owchPanel.add(ParentURLText);
/*  51 */     IRCPanel.setLayout(new BoxLayout(IRCPanel, 1));
/*  52 */     IRCPanel.add(IRCHostText);
/*  53 */     IRCPanel.add(IRCPortText);
/*  54 */     IRCPanel.add(IRCNicknameText);
/*  55 */     IRCPanel.add(IRCJoinText);
/*  56 */     AgentDescriptor.setBounds(new Rectangle(15, 24, 399, 321));
/*  57 */     AgentDescriptor.setVisible(true);
/*  58 */     AgentDescriptor.setTitle("AgentDescriptor");
/*  59 */     AgentDescriptor.setResizable(true);
/*  60 */     AgentDescriptor.setMaximizable(true);
/*  61 */     AgentDescriptor.setIconifiable(true);
/*     */ 
/*  64 */     desktop.add(AgentDescriptor);
/*  65 */     connectCheck.setText("Connect");
/*  66 */     connectCheck.setVerticalAlignment(0);
/*  67 */     connectCheck.setHorizontalTextPosition(10);
/*     */ 
/*  69 */     agentToolbar.add(connectCheck);
/*  70 */     connectCheck.addActionListener(new ActionListener()
/*     */     {
/*     */       public void actionPerformed(ActionEvent e) {
/*  73 */         connectCheckActionPerformed(e);
/*     */       }
/*     */     });
/*  76 */     getContentPane().setLayout(new BorderLayout());
/*  77 */     getContentPane().add(desktop, "Center");
/*  78 */     getContentPane().add(channelBar, "South");
/*  79 */     getContentPane().add(agentToolbar, "North");
/*  80 */     AgentDescriptor.getContentPane().add(agentTabs, "Center");
/*  81 */     setBounds(new Rectangle(0, 0, 694, 494));
/*  82 */     setTitle("owch2 IRC Agent UI");
/*  83 */     setDefaultCloseOperation(3);
/*  84 */     agentTabs.setToolTipText("");
/*  85 */     agentTabs.setTabPlacement(1);
/*  86 */     agentTabs.setBorder(BorderFactory.createEtchedBorder());
/*  87 */     agentTabs.setVerifyInputWhenFocusTarget(true);
/*  88 */     agentTabs.add(IRCPanel, "IRC");
/*  89 */     agentTabs.add(owchPanel, "owch2 Agent");
/*  90 */     setVisible(true);
/*     */   }
/*     */ 
/*     */   public String[] getApp_keys() {
/*  94 */     return app_keys;
/*     */   }
/*     */ 
/*     */   public String getApp_keys(int index) {
/*  98 */     return app_keys[index];
/*     */   }
/*     */ 
/*     */   public void connectCheckActionPerformed(ActionEvent e)
/*     */   {
/* 111 */     boolean flag = ((JCheckBox)e.getSource()).isSelected();
/* 112 */     if (flag) {
/* 113 */       startAgent();
/*     */     }
/*     */     else
/* 116 */       stopAgent();
/*     */   }
/*     */ 
/*     */   public AbstractAgent getNode()
/*     */   {
/* 121 */     return node;
/*     */   }
/*     */ 
/*     */   public void setNode(AbstractAgent node) {
/* 125 */     this.node = node;
/*     */   }
/*     */ 
/*     */   public void startAgent() {
/* 129 */     Location l = new Location();
/*     */ 
/* 132 */     for (int i = 0; i < getApp_keys().length; i++) {
/* 133 */       String s = (String)get(getApp_keys(i));
/* 134 */       if (s != null) {
/* 135 */         s = s.trim();
/*     */       }
/* 137 */       if (s.length() > 0) {
/* 138 */         l.put(getApp_keys(i), s);
/*     */       }
/*     */     }
/*     */ 
/* 142 */     AgentDescriptor.setEnabled(false);
/* 143 */     Env.log(509, l.toString());
/* 144 */     setNode(new IRCManager(this, l));
/*     */   }
/*     */ 
/*     */   public Object get(Object key)
/*     */   {
/*     */     try {
/* 150 */       String key1 = key.toString();
/* 151 */       Class c = getClass();
/* 152 */       Env.log(5, "get::" + key1 + "Text");
/* 153 */       Field f = c.getField(key1 + "Text");
/* 154 */       Env.log(5, "getf::" + f.toString());
/* 155 */       Object o = f.get(this);
/* 156 */       Method m = o.getClass().getMethod("getText", AgentVisitor.no_class);
/* 157 */       Env.log(5, "getm::" + m.toString());
/* 158 */       return m.invoke(o, AgentVisitor.no_Parm).toString();
/*     */     }
/*     */     catch (NoSuchFieldException e)
/*     */     {
/* 162 */       return getNode().get(key);
/*     */     }
/*     */     catch (SecurityException e) {
/* 165 */       return getNode().get(key);
/*     */     }
/*     */     catch (IllegalArgumentException e) {
/* 168 */       return getNode().get(key);
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 171 */       return getNode().get(key);
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/* 174 */       return getNode().get(key);
/*     */     } catch (InvocationTargetException e) {
/*     */     }
/* 177 */     return getNode().get(key);
/*     */   }
/*     */ 
/*     */   public void stopAgent()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void put(Object key, Object val)
/*     */   {
/*     */     try
/*     */     {
/* 191 */       String key1 = key.toString();
/* 192 */       Class c = getClass();
/* 193 */       Env.log(5, "get::" + key1 + "Text");
/* 194 */       Field f = c.getField(key1 + "Text");
/* 195 */       Env.log(5, "get::" + f.toString());
/* 196 */       Object o = f.get(this);
/* 197 */       Method m = o.getClass().getMethod("setText", new Class[] { String.class });
/*     */ 
/* 199 */       m.invoke(o, new Object[] { val });
/*     */     }
/*     */     catch (NoSuchFieldException e)
/*     */     {
/*     */     }
/*     */     catch (SecurityException e) {
/*     */     }
/*     */     catch (IllegalArgumentException e) {
/*     */     }
/*     */     catch (IllegalAccessException e) {
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/*     */     }
/*     */     catch (InvocationTargetException e) {
/*     */     }
/*     */     finally {
/* 215 */       getNode().put(key, val);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.gui.IRC.IRCVisitor
 * JD-Core Version:    0.6.0
 */