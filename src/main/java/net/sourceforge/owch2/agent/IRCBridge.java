/*    */ package net.sourceforge.owch2.agent;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.StringTokenizer;
/*    */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*    */ import net.sourceforge.owch2.kernel.Env;
/*    */ import net.sourceforge.owch2.kernel.MetaProperties;
/*    */ import net.sourceforge.owch2.kernel.Notification;
/*    */ 
/*    */ public class IRCBridge extends AbstractAgent
/*    */ {
/* 11 */   String[] agents = { "", "" };
/*    */ 
/*    */   public IRCBridge(Map m) {
/* 14 */     super(m);
/* 15 */     super.relocate();
/* 16 */     String aaaa = get("IRCAgents").toString();
/* 17 */     setAgents(aaaa);
/*    */   }
/*    */ 
/*    */   public void setAgents(String agentsIn)
/*    */   {
/* 22 */     StringTokenizer st = new StringTokenizer(agentsIn);
/*    */ 
/* 24 */     List l = new LinkedList();
/* 25 */     while (st.hasMoreElements()) {
/* 26 */       String s = (String)st.nextElement();
/* 27 */       l.add(s);
/*    */     }
/* 29 */     agents = ((String[])l.toArray(agents));
/*    */   }
/*    */ 
/*    */   public void handle_IRC_PRIVMSG(MetaProperties m)
/*    */   {
/* 34 */     String ircAgent = m.get("IRCAgent").toString();
/* 35 */     String ircNickName = m.get("JMSReplyTo").toString();
/* 36 */     String preliminaryValue = m.get("Value").toString();
/* 37 */     String finalValue = "<" + ircNickName + "@" + ircAgent + "> " + preliminaryValue;
/*    */ 
/* 41 */     for (int i = 0; i < agents.length; i++) {
/* 42 */       String agent = agents[i];
/* 43 */       if (ircAgent.equals(agent))
/*    */         continue;
/* 45 */       Notification repeatedMessage = new Notification(m);
/* 46 */       repeatedMessage.put("JMSType", "MSG"); repeatedMessage.put("IRCChannel", getJMSReplyTo());
/* 47 */       repeatedMessage.put("JMSDestination", agent);
/* 48 */       repeatedMessage.put("Value", finalValue);
/* 49 */       Env.log(448, getJMSReplyTo() + ">>" + repeatedMessage.toString());
/* 50 */       send(repeatedMessage);
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) {
/* 55 */     Map m = Env.parseCommandLineArgs(args);
/* 56 */     String[] ka = { "JMSReplyTo", "IRCAgents" };
/*    */ 
/* 58 */     if (!m.keySet().containsAll(Arrays.asList(ka))) {
/* 59 */       Env.cmdLineHelp("\n\n******************** cmdline syntax error\nIRCBridge usage:\n\n-name (String)name --(channel name e.g. #python)\n-IRCAgents (String)'agent1[ agent..n]'\n[-Deploy 'host1[ ..hostn]']\n$Id: IRCBridge.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
/*    */     }
/*    */ 
/* 66 */     IRCBridge d = new IRCBridge(m);
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.IRCBridge
 * JD-Core Version:    0.6.0
 */