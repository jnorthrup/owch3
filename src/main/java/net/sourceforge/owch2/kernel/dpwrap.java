/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.DatagramPacket;
/*    */ import java.net.DatagramSocket;
/*    */ import java.net.InetAddress;
/*    */ 
/*    */ final class dpwrap
/*    */   implements BehaviorState
/*    */ {
/*    */   DatagramPacket p;
/*  8 */   int count = 0;
/*    */ 
/*    */   dpwrap(DatagramPacket p_)
/*    */   {
/* 12 */     p = p_;
/*    */   }
/*    */ 
/*    */   final byte[] getData() {
/* 16 */     return p.getData();
/*    */   }
/*    */ 
/*    */   final InetAddress getAddress() {
/* 20 */     return p.getAddress();
/*    */   }
/*    */ 
/*    */   final int getPort() {
/* 24 */     return p.getPort();
/*    */   }
/*    */ 
/*    */   public byte fire() throws IOException
/*    */   {
/* 29 */     count += 1;
/*    */     DatagramSocket ds;
/* 31 */     if (count < 12) {
/* 32 */       ds = (DatagramSocket)Env.getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
/* 33 */       ds.send(p);
/* 34 */       return 0;
/*    */     }
/* 36 */     if (count % 12 == 0)
/*    */     {
/* 38 */       ds = (DatagramSocket)Env.getProtocolCache().getListenerCache("owch").getNextInLine().getServer();
/* 39 */       ds.send(p);
/* 40 */       return 1;
/*    */     }
/* 42 */     if (count > 144) {
/* 43 */       Env.log(30, "debug:  dpwrap timeout");
/* 44 */       return 3;
/*    */     }
/* 46 */     return 2;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.dpwrap
 * JD-Core Version:    0.6.0
 */