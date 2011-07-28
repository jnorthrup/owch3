/*    */ package net.sourceforge.gui.IRC;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import java.util.StringTokenizer;
/*    */ import javax.swing.JList;
/*    */ import net.sourceforge.gui.ScrollingListModel;
/*    */ import net.sourceforge.nlp.SentenceParser;
/*    */ import net.sourceforge.nlp.Serializer;
/*    */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*    */ import net.sourceforge.owch2.kernel.Env;
/*    */ import net.sourceforge.owch2.kernel.Location;
/*    */ import net.sourceforge.owch2.kernel.MetaProperties;
/*    */ 
/*    */ public class IRCChannelAgent extends AbstractAgent
/*    */ {
/*    */   IRCChannelGUI gui;
/*    */   private SentenceParser sParser;
/*    */ 
/*    */   public IRCChannelAgent(IRCChannelGUI g, Location l)
/*    */   {
/* 15 */     super(l);
/*    */     try {
/* 17 */       sParser = new SentenceParser(getJMSReplyTo() + ".hist");
/*    */     }
/*    */     catch (Exception e) {
/*    */       try {
/* 21 */         Serializer ser = new Serializer();
/* 22 */         ser.serialize(getJMSReplyTo() + ".hist");
/* 23 */         sParser = new SentenceParser(getJMSReplyTo() + ".hist");
/*    */       }
/*    */       catch (Exception e1) {
/* 26 */         e1.printStackTrace();
/*    */       }
/*    */     }
/* 29 */     gui = g;
/*    */   }
/*    */ 
/*    */   public void handle_Dissolve(MetaProperties n)
/*    */   {
/* 34 */     n.put("JMSDestination", get("IRCManager"));
/* 35 */     n.put("JMSType", "PART");
/* 36 */     n.put("Value", getJMSReplyTo());
/* 37 */     n.put("JMSReplyTo", getJMSReplyTo());
/* 38 */     send(n);
/* 39 */     sParser.write(getJMSReplyTo() + ".hist");
/* 40 */     super.handle_Dissolve(new Location(this));
/*    */   }
/*    */ 
/*    */   public void handle_IRC_PRIVMSG(MetaProperties m)
/*    */   {
/* 46 */     String value = m.get("Value").toString();
/* 47 */     ScrollingListModel lm = (ScrollingListModel)gui.getMsgList().getModel();
/* 48 */     List l = sParser.tokenize(value);
/* 49 */     Iterator i = l.iterator();
/* 50 */     while (i.hasNext()) lm.addElement(i.next());
/* 51 */     while (lm.getSize() > 1000)
/* 52 */       lm.remove(0);
/*    */   }
/*    */ 
/*    */   public void handle_IRC_PRIVMSG2(MetaProperties m)
/*    */   {
/* 59 */     String value = m.get("Value").toString();
/* 60 */     ScrollingListModel lm = (ScrollingListModel)gui.getMsgList().getModel();
/* 61 */     String ret = "<" + m.get("JMSReplyTo") + "> " + value;
/*    */ 
/* 64 */     lm.addElement(ret);
/* 65 */     while (lm.getSize() > 1000) {
/* 66 */       lm.remove(0);
/*    */     }
/* 68 */     sParser.tokenize(value);
/*    */   }
/*    */ 
/*    */   public void handle_IRC_RPL_NAMREPLY(MetaProperties m) {
/* 72 */     StringTokenizer tk = new StringTokenizer(m.get("Value").toString());
/* 73 */     ScrollingListModel lm = (ScrollingListModel)gui.getUsersList().getModel();
/*    */ 
/* 75 */     while (tk.hasMoreTokens()) {
/* 76 */       lm.addElement(tk.nextToken());
/*    */     }
/* 78 */     Env.log(50, "recvd names");
/*    */   }
/*    */ 
/*    */   public void handle_IRC_RPL_ENDOFNAMES(MetaProperties m) {
/* 82 */     gui.getUsersList().invalidate();
/* 83 */     gui.getUsersList().repaint();
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.gui.IRC.IRCChannelAgent
 * JD-Core Version:    0.6.0
 */