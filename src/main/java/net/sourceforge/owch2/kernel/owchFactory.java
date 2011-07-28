/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ 
/*    */ public final class owchFactory extends ListenerFactory
/*    */ {
/*    */   public owchFactory()
/*    */   {
/* 13 */     Env.log(200, "DataGramFactory instantiated");
/*    */   }
/*    */ 
/*    */   public ListenerReference create(InetAddress hostAddr, int port, int threads) {
/* 17 */     Thread t = null;
/* 18 */     owchListener udps = null;
/*    */     try {
/* 20 */       udps = new owchListener(hostAddr, port, threads);
/*    */     }
/*    */     catch (Exception e) {
/* 23 */       Env.log(2, "owchListener failure on port " + port);
/* 24 */       e.printStackTrace();
/*    */     }
/*    */ 
/* 27 */     for (int i = 0; i < Env.getHostThreads(); i++) {
/* 28 */       t = new Thread(udps, "owchListener Thread #" + i + " / port " + udps.getLocalPort());
/* 29 */       t.setDaemon(true);
/* 30 */       t.start();
/*    */     }
/*    */ 
/* 33 */     return udps;
/*    */   }
/*    */ 
/*    */   public final MetaProperties getLocation() {
/* 37 */     return Env.getProtocolCache().getLocation("owch");
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.owchFactory
 * JD-Core Version:    0.6.0
 */