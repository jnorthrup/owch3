/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.InetAddress;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ 
/*    */ public class TCPServerWrapper
/*    */   implements ServerWrapper
/*    */ {
/*    */   ServerSocket s;
/*    */ 
/*    */   public TCPServerWrapper(int port, InetAddress hostAddr)
/*    */     throws IOException
/*    */   {
/* 21 */     s = new ServerSocket(port, 16, hostAddr);
/*    */   }
/*    */ 
/*    */   public final void close() {
/*    */     try {
/* 26 */       s.close();
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public final Socket accept() throws IOException {
/* 34 */     return s.accept();
/*    */   }
/*    */ 
/*    */   public final int getLocalPort() {
/* 38 */     return s.getLocalPort();
/*    */   }
/*    */ 
/*    */   public final ServerSocket serverSocket() {
/* 42 */     return s;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.TCPServerWrapper
 * JD-Core Version:    0.6.0
 */