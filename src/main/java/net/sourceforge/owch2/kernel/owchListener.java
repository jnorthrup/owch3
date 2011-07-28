/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.DatagramPacket;
/*    */ import java.net.InetAddress;
/*    */ import java.net.SocketException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.DatagramPacketFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.DatagramPacketSource;
/*    */ import net.sourceforge.idyuts.IOUtil.Auto;
/*    */ 
/*    */ public class owchListener extends UDPServerWrapper
/*    */   implements Runnable, ListenerReference, DatagramPacketSource
/*    */ {
/*    */   private int threads;
/* 67 */   private List _DatagramPacket_clients = new ArrayList();
/*    */   private DatagramPacket data;
/* 79 */   private static final Class[] parm_cls_DatagramPacket = { DatagramPacket.class };
/*    */ 
/*    */   public owchListener(InetAddress hostAddr, int port, int threads)
/*    */     throws SocketException
/*    */   {
/* 19 */     super(hostAddr, port);
/* 20 */     this.threads = threads;
/* 21 */     Auto.attach(this, Env.getNotificationFactory());
/*    */   }
/*    */ 
/*    */   public owchListener(InetAddress hostAddr, int port) throws SocketException {
/* 25 */     super(hostAddr, port);
/*    */   }
/*    */ 
/*    */   public final void run() {
/* 29 */     Env.log(20, "debug: " + Thread.currentThread().getName() + " init");
/* 30 */     byte[] bar = new byte[32768];
/*    */     while (true) {
/*    */       try {
/* 33 */         DatagramPacket p = new DatagramPacket(bar, bar.length);
/* 34 */         receive(p);
/* 35 */         data = p;
/* 36 */         xmit();
/* 37 */         Env.log(12, "debug: spin, " + Thread.currentThread().getName());
/*    */       }
/*    */       catch (IOException e) {
/* 40 */         Env.log(5, "debug: OWCH RUN BREAK");
/* 41 */         break;
/*    */       }
/*    */     }
/* 44 */     Env.log(5, "debug: OWCH THREAD STOP");
/*    */   }
/*    */ 
/*    */   public String getProtocol() {
/* 48 */     return "owch";
/*    */   }
/*    */ 
/*    */   public long getExpiration() {
/* 52 */     return 0L;
/*    */   }
/*    */ 
/*    */   public int getThreads() {
/* 56 */     return threads;
/*    */   }
/*    */ 
/*    */   public ServerWrapper getServer() {
/* 60 */     return this;
/*    */   }
/*    */ 
/*    */   public void expire() {
/* 64 */     getServer().close();
/*    */   }
/*    */ 
/*    */   public void attach(DatagramPacketFilter filter)
/*    */   {
/* 70 */     _DatagramPacket_clients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(DatagramPacketFilter filter) {
/* 74 */     _DatagramPacket_clients.remove(filter);
/*    */   }
/*    */ 
/*    */   public void xmit()
/*    */   {
/* 84 */     Iterator iter = _DatagramPacket_clients.iterator();
/* 85 */     while (iter.hasNext()) {
/* 86 */       DatagramPacketFilter filter = (DatagramPacketFilter)iter.next();
/* 87 */       filter.recv(data);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.owchListener
 * JD-Core Version:    0.6.0
 */