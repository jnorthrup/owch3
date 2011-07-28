/*     */ package net.sourceforge.gui.IRC;
/*     */ 
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Container;
/*     */ import java.awt.Rectangle;
/*     */ import javax.swing.JInternalFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JScrollPane;
/*     */ import javax.swing.JSplitPane;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.JToolBar;
/*     */ import javax.swing.JViewport;
/*     */ import javax.swing.event.InternalFrameAdapter;
/*     */ import javax.swing.event.InternalFrameEvent;
/*     */ import net.sourceforge.gui.AgentVisitor;
/*     */ import net.sourceforge.gui.ScrollingListModel;
/*     */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*     */ import net.sourceforge.owch2.kernel.Env;
/*     */ import net.sourceforge.owch2.kernel.Location;
/*     */ import net.sourceforge.owch2.kernel.MetaProperties;
/*     */ import net.sourceforge.owch2.kernel.Notification;
/*     */ 
/*     */ public class IRCChannelGUI extends JInternalFrame
/*     */   implements AgentVisitor
/*     */ {
/*  12 */   private JList UsersList = new JList(new ScrollingListModel());
/*  13 */   private JToolBar EntryDoc = new JToolBar();
/*  14 */   private JList msgList = new JList(new ScrollingListModel());
/*  15 */   private JTextField ValueText = new JTextField();
/*     */   Location agentLocation;
/*     */   IRCChannelAgent node;
/*     */ 
/*     */   public IRCChannelGUI(MetaProperties JoinMsg)
/*     */   {
/*  20 */     agentLocation = new Location(JoinMsg);
/*  21 */     agentLocation.put("IRCManager", agentLocation.get("JMSReplyTo"));
/*  22 */     agentLocation.put("JMSReplyTo", agentLocation.get("Value"));
/*  23 */     initGUI();
/*  24 */     startAgent();
/*     */   }
/*     */ 
/*     */   public void initGUI()
/*     */   {
/*  30 */     JLabel Value = new JLabel();
/*  31 */     JScrollPane msgScroll = new JScrollPane();
/*  32 */     JScrollPane userScroll = new JScrollPane();
/*  33 */     JSplitPane mainSplitter = new JSplitPane();
/*  34 */     JTextArea renderPane = new JTextArea();
/*     */ 
/*  36 */     msgScroll.getViewport().add(getMsgList());
/*  37 */     msgScroll.getViewport().add(msgList);
/*  38 */     userScroll.getViewport().add(getUsersList());
/*  39 */     userScroll.getViewport().add(UsersList);
/*  40 */     getContentPane().setLayout(new BorderLayout());
/*  41 */     getContentPane().add(mainSplitter, "Center");
/*  42 */     getContentPane().add(EntryDoc, "South");
/*  43 */     setBounds(new Rectangle(0, 0, 541, 360));
/*  44 */     setIconifiable(true);
/*  45 */     setClosable(true);
/*  46 */     setMaximizable(true);
/*  47 */     EntryDoc.add(Value);
/*  48 */     EntryDoc.add(ValueText);
/*  49 */     Value.setText(">>");
/*  50 */     setResizable(true);
/*  51 */     addInternalFrameListener(new InternalFrameAdapter()
/*     */     {
/*     */       public void internalFrameClosing(InternalFrameEvent e) {
/*  54 */         stopAgent();
/*     */       }
/*     */     });
/*  57 */     mainSplitter.setBounds(new Rectangle(116, 135, 180, 29));
/*  58 */     mainSplitter.setOneTouchExpandable(true);
/*  59 */     mainSplitter.setDividerLocation(400);
/*  60 */     mainSplitter.add(msgScroll, "left");
/*  61 */     mainSplitter.add(userScroll, "right");
/*     */ 
/*  71 */     renderPane.setWrapStyleWord(true);
/*  72 */     renderPane.setLineWrap(true);
/*     */   }
/*     */ 
/*     */   public Object get(Object key) {
/*  76 */     return null;
/*     */   }
/*     */ 
/*     */   public void put(Object key, Object val) {
/*     */   }
/*     */ 
/*     */   public void stopAgent() {
/*  83 */     Notification notification = new Notification();
/*  84 */     notification.put("JMSType", "Dissolve");
/*  85 */     notification.put("JMSReplyTo", node.get("IRCManager"));
/*  86 */     notification.put("JMSDestination", node.getJMSReplyTo());
/*  87 */     Env.send(notification);
/*     */   }
/*     */ 
/*     */   public void startAgent() {
/*  91 */     node = new IRCChannelAgent(this, agentLocation);
/*  92 */     setTitle(agentLocation.get("Value").toString());
/*     */   }
/*     */ 
/*     */   public String[] getApp_keys()
/*     */   {
/* 100 */     return new String[0];
/*     */   }
/*     */ 
/*     */   public String getApp_keys(int index) {
/* 104 */     return new String();
/*     */   }
/*     */ 
/*     */   public AbstractAgent getNode() {
/* 108 */     return node;
/*     */   }
/*     */ 
/*     */   public JList getMsgList() {
/* 112 */     return msgList;
/*     */   }
/*     */ 
/*     */   private void setMsgList(JList msgList) {
/* 116 */     this.msgList = msgList;
/*     */   }
/*     */ 
/*     */   public JList getUsersList() {
/* 120 */     return UsersList;
/*     */   }
/*     */ 
/*     */   protected void setUsersList(JList UsersList) {
/* 124 */     this.UsersList = UsersList;
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.gui.IRC.IRCChannelGUI
 * JD-Core Version:    0.6.0
 */