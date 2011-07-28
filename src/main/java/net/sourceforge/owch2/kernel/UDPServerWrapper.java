/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.net.DatagramSocket;
/*    */ import java.net.InetAddress;
/*    */ import java.net.SocketException;
/*    */ 
/*    */ public class UDPServerWrapper extends DatagramSocket
/*    */   implements ServerWrapper
/*    */ {
/*    */   public UDPServerWrapper(InetAddress hostAddr, int port)
/*    */     throws SocketException
/*    */   {
/* 17 */     super(port, hostAddr);
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.UDPServerWrapper
 * JD-Core Version:    0.6.0
 */