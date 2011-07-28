/*    */ package net.sourceforge.gui.IRC;
/*    */ 
/*    */ import javax.swing.JDesktopPane;
/*    */ import net.sourceforge.owch2.agent.IRC;
/*    */ import net.sourceforge.owch2.kernel.MetaProperties;
/*    */ 
/*    */ public class IRCManager extends IRC
/*    */ {
/*    */   IRCVisitor MainFrame;
/*    */ 
/*    */   public void handle_IRC_JOIN(MetaProperties m)
/*    */   {
/*  9 */     if (m.get("JMSReplyTo").toString().equals(get("IRCNickname").toString())) {
/* 10 */       m.put("JMSReplyTo", getJMSReplyTo());
/* 11 */       IRCChannelGUI cg = new IRCChannelGUI(m);
/* 12 */       MainFrame.desktop.add(cg);
/* 13 */       cg.setVisible(true);
/*    */     }
/*    */   }
/*    */ 
/*    */   public IRCManager(IRCVisitor a, MetaProperties p)
/*    */   {
/* 20 */     super(p);
/* 21 */     MainFrame = a;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.gui.IRC.IRCManager
 * JD-Core Version:    0.6.0
 */