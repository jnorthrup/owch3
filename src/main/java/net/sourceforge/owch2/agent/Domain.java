/*    */ package net.sourceforge.owch2.agent;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import net.sourceforge.owch2.kernel.Env;
/*    */ import net.sourceforge.owch2.router.DefaultRouteHunter;
/*    */ 
/*    */ public class Domain extends Deploy
/*    */ {
/*    */   public Domain(Map p)
/*    */   {
/* 16 */     super(p);
/* 17 */     Env.getLocation("owch");
/* 18 */     Env.getLocation("http");
/* 19 */     Env.setParentHost(true);
/* 20 */     Env.setRouteHunter(new DefaultRouteHunter());
/*    */   }
/*    */ 
/*    */   public final boolean isParent()
/*    */   {
/* 25 */     return true;
/*    */   }
/*    */ 
/*    */   public static void main(String[] args) throws Exception {
/* 29 */     Map m = Env.parseCommandLineArgs(args);
/* 30 */     String portString = "HostPort";
/* 31 */     String[] ka = { "JMSReplyTo", "HostPort" };
/*    */ 
/* 33 */     if (!m.keySet().containsAll(Arrays.asList(ka))) {
/* 34 */       Env.cmdLineHelp("\n\n******************** cmdline syntax error\nDomain Agent usage:\n\n-JMSReplyTo (String)name\n-HostPort (int)port\n$Id: Domain.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
/*    */     }
/*    */ 
/* 41 */     Domain d = static_init(m, "HostPort");
/*    */ 
/* 44 */     static_spin(d);
/*    */   }
/*    */ 
/*    */   private static void static_spin(Domain d) throws InterruptedException {
/* 48 */     while (!killFlag) {
/* 49 */       Thread.currentThread(); Thread.sleep(60000L);
/*    */     }
/*    */   }
/*    */ 
/*    */   private static Domain static_init(Map m, String portString) {
/* 54 */     Env.setParentHost(true);
/* 55 */     String s = m.get(portString).toString();
/* 56 */     int port = Integer.parseInt(s);
/* 57 */     Env.setHostPort(port);
/*    */ 
/* 59 */     Domain d = new Domain(m);
/* 60 */     return d;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.Domain
 * JD-Core Version:    0.6.0
 */