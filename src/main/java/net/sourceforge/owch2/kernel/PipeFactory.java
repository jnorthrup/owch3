/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ 
/*    */ public class PipeFactory extends ListenerFactory
/*    */ {
/*    */   public final MetaProperties getLocation()
/*    */   {
/* 14 */     return Env.getProtocolCache().getLocation("pipe");
/*    */   }
/* 18 */   public ListenerReference create(InetAddress hostAddr, int port, int threads) { Thread t = null;
/*    */     PipeConnector Pipes;
/*    */     try {
/* 21 */       Pipes = new PipeConnector(hostAddr, port, threads);
/*    */     }
/*    */     catch (Exception e) {
/* 24 */       Env.log(2, "PipeConnector init failure port " + port);
/* 25 */       return null;
/*    */     }
/* 27 */     for (int i = 0; i < Pipes.getThreads(); i++) {
/* 28 */       t = new Thread(Pipes, "PipeListener Thread #" + i + " / port " + Pipes.getLocalPort());
/* 29 */       t.setDaemon(true);
/* 30 */       t.start();
/*    */     }
/* 32 */     return Pipes;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.PipeFactory
 * JD-Core Version:    0.6.0
 */