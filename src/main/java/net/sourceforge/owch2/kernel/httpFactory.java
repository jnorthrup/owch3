/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ 
/*    */ public class httpFactory extends ListenerFactory
/*    */ {
/*    */   public MetaProperties getLocation()
/*    */   {
/* 14 */     return Env.getProtocolCache().getLocation("http");
/*    */   }
/* 18 */   public ListenerReference create(InetAddress hostAddr, int port, int threads) { Thread t = null;
/*    */     httpServer https;
/*    */     try {
/* 21 */       https = new httpServer(hostAddr, port, threads);
/*    */     }
/*    */     catch (Exception e) {
/* 24 */       Env.log(2, "httpServer init failure port " + port);
/* 25 */       return null;
/*    */     }
/*    */ 
/* 28 */     for (int i = 0; i < https.getThreads(); i++) {
/* 29 */       t = new Thread(https, "httpListener Thread #" + i + " / port " + https.getLocalPort());
/* 30 */       t.setDaemon(true);
/* 31 */       t.start();
/*    */     }
/*    */ 
/* 34 */     return https;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.httpFactory
 * JD-Core Version:    0.6.0
 */