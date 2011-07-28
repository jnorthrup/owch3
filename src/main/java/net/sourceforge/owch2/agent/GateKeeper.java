/*    */ package net.sourceforge.owch2.agent;
/*    */ 
/*    */ import java.util.Map;
/*    */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*    */ import net.sourceforge.owch2.kernel.Env;
/*    */ import net.sourceforge.owch2.kernel.MetaProperties;
/*    */ import net.sourceforge.owch2.kernel.httpRegistry;
/*    */ 
/*    */ public class GateKeeper extends AbstractAgent
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 17 */     Map m = Env.parseCommandLineArgs(args);
/* 18 */     if ((!m.containsKey("JMSReplyTo")) || (!m.containsKey("HostPort"))) {
/* 19 */       Env.cmdLineHelp("\n\n******************** cmdline syntax error\nGateKeeper Agent usage:\n\n-name name\n-HostPort port\n$Id: GateKeeper.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
/*    */     }
/*    */ 
/* 23 */     GateKeeper d = new GateKeeper(m);
/* 24 */     Thread t = new Thread();
/*    */     try {
/* 26 */       t.start();
/*    */       while (true)
/* 28 */         Thread.sleep(60000L);
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public void handle_Register(MetaProperties notificationIn)
/*    */   {
/*    */     try
/*    */     {
/* 42 */       String Item = notificationIn.get("URLSpec").toString();
/* 43 */       notificationIn.put("URL", notificationIn.get("URLFwd"));
/* 44 */       Env.gethttpRegistry().registerItem(Item, notificationIn);
/* 45 */       return;
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public void handle_UnRegister(MetaProperties notificationIn)
/*    */   {
/*    */     try {
/* 55 */       String Item = notificationIn.get("URLSpec").toString();
/* 56 */       notificationIn.put("URL", notificationIn.get("URLFwd"));
/* 57 */       Env.gethttpRegistry().unregisterItem(Item);
/* 58 */       return;
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public GateKeeper(Map m)
/*    */   {
/* 71 */     super(m);
/* 72 */     Env.getLocation("http");
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.GateKeeper
 * JD-Core Version:    0.6.0
 */