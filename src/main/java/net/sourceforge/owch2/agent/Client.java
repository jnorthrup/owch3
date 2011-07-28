/*    */ package net.sourceforge.owch2.agent;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.net.URL;
/*    */ import java.net.URLClassLoader;
/*    */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*    */ import net.sourceforge.owch2.kernel.MetaProperties;
/*    */ import net.sourceforge.owch2.kernel.Notification;
/*    */ 
/*    */ public class Client extends AbstractAgent
/*    */   implements Runnable
/*    */ {
/* 25 */   static String host = "localhost";
/* 26 */   static int port = 2112;
/* 27 */   static String JMSReplyTo = "Client";
/*    */ 
/*    */   public static void main(String[] args) {
/* 30 */     System.out.println(args);
/* 31 */     if (args.length > 2) {
/* 32 */       host = args[2];
/*    */     }
/* 34 */     if (args.length > 1) {
/* 35 */       port = Integer.valueOf(args[1]).intValue();
/*    */     }
/* 37 */     if (args.length > 0) {
/* 38 */       JMSReplyTo = args[0];
/*    */     }
/* 40 */     new Client();
/*    */   }
/*    */ 
/*    */   public Client()
/*    */   {
/* 50 */     put("JMSReplyTo", JMSReplyTo);
/* 51 */     linkTo("Main");
/* 52 */     MetaProperties n = new Notification();
/* 53 */     n.put("JMSDestination", "Main");
/* 54 */     n.put("JMSType", "Test");
/* 55 */     send(n);
/*    */     while (true)
/*    */       try {
/* 58 */         Thread.currentThread(); Thread.sleep(200000L);
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/*    */       }
/*    */   }
/*    */ 
/*    */   public void run()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void handle_Test(MetaProperties notificationIn)
/*    */   {
/*    */     try
/*    */     {
/* 77 */       URL p1 = new URL(notificationIn.get("Path").toString());
/* 78 */       URLClassLoader loader = new URLClassLoader(new URL[] { p1 });
/*    */ 
/* 80 */       loader.loadClass(notificationIn.get("Class").toString()).newInstance();
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.Client
 * JD-Core Version:    0.6.0
 */