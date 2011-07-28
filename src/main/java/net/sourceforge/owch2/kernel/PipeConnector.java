/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import java.net.Socket;
/*    */ 
/*    */ public class PipeConnector extends TCPServerWrapper
/*    */   implements ListenerReference, Runnable
/*    */ {
/*    */   int threads;
/*    */ 
/*    */   public String getProtocol()
/*    */   {
/* 15 */     return "pipe";
/*    */   }
/*    */ 
/*    */   public long getExpiration() {
/* 19 */     return 0L;
/*    */   }
/*    */ 
/*    */   public int getThreads() {
/* 23 */     return threads;
/*    */   }
/*    */ 
/*    */   public ServerWrapper getServer() {
/* 27 */     return this;
/*    */   }
/*    */ 
/*    */   public void expire() {
/* 31 */     getServer().close();
/*    */   }
/*    */ 
/*    */   PipeConnector(InetAddress hostAddr, int port, int threads) throws IOException {
/* 35 */     super(port, hostAddr);
/* 36 */     this.threads = threads;
/*    */     try {
/* 38 */       for (int i = 0; i < threads; i++)
/* 39 */         new Thread(this).start();
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 43 */       Env.log(2, "ServerSocket creation Failure:" + e.getMessage());
/*    */     }
/*    */   }
/*    */ 
/*    */   public void run() {
/* 48 */     while (!Env.shutdown)
/*    */       try {
/* 50 */         Socket s = accept();
/* 51 */         Env.log(20, "debug: " + Thread.currentThread().getName() + " init");
/*    */       }
/*    */       catch (Exception e) {
/* 54 */         Env.log(2, "PipeServer thread going down in flames");
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.PipeConnector
 * JD-Core Version:    0.6.0
 */