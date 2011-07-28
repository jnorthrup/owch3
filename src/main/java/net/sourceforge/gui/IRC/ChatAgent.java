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
/*    */ public class ChatAgent extends AbstractAgent
/*    */ {
/*    */   ChatGUI gui;
/*    */   private SentenceParser sParser;
/*    */ 
/*    */   public ChatAgent(ChatGUI g, Location l)
/*    */   {
/* 14 */     super(l);
/*    */     try {
/* 16 */       sParser = new SentenceParser(getJMSReplyTo() + ".hist");
/*    */     }
/*    */     catch (Exception e) {
/*    */       try {
/* 20 */         Serializer ser = new Serializer();
/* 21 */         ser.serialize(getJMSReplyTo() + ".hist");
/* 22 */         sParser = new SentenceParser(getJMSReplyTo() + ".hist");
/*    */       }
/*    */       catch (Exception e1) {
/* 25 */         e1.printStackTrace();
/*    */       }
/*    */     }
/*    */ 
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
/*    */   public void handle_IRC_PRIVMSG(MetaProperties m) {
/* 44 */     String value = m.get("Value").toString();
/* 45 */     ScrollingListModel lm = (ScrollingListModel)gui.getMsgList().getModel();
/* 46 */     List l = sParser.tokenize(value);
/* 47 */     Iterator i = l.iterator();
/* 48 */     while (i.hasNext()) lm.addElement(i.next());
/* 49 */     while (lm.getSize() > 1000)
/* 50 */       lm.remove(0);
/*    */   }
/*    */ 
/*    */   public void handle_IRC_PRIVMSG2(MetaProperties m)
/*    */   {
/* 58 */     String value = m.get("Value").toString();
/* 59 */     ScrollingListModel lm = (ScrollingListModel)gui.getMsgList().getModel();
/* 60 */     String ret = "<" + m.get("JMSReplyTo") + "> " + value;
/* 61 */     lm.addElement(ret);
/* 62 */     while (lm.getSize() > 1000) {
/* 63 */       lm.remove(0);
/*    */     }
/* 65 */     sParser.tokenize(value);
/*    */   }
/*    */ 
/*    */   public void handle_IRC_RPL_NAMREPLY(MetaProperties m) {
/* 69 */     StringTokenizer tk = new StringTokenizer(m.get("Value").toString());
/* 70 */     ScrollingListModel lm = (ScrollingListModel)gui.getUsersList().getModel();
/*    */ 
/* 72 */     while (tk.hasMoreTokens()) {
/* 73 */       lm.addElement(tk.nextToken());
/*    */     }
/*    */ 
/* 76 */     Env.log(50, "recvd names");
/*    */   }
/*    */ 
/*    */   public void handle_IRC_RPL_ENDOFNAMES(MetaProperties m) {
/* 80 */     gui.getUsersList().invalidate();
/* 81 */     gui.getUsersList().repaint();
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.gui.IRC.ChatAgent
 * JD-Core Version:    0.6.0
 */