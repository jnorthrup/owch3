/*     */ package net.sourceforge.owch2.kernel;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.SocketException;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import net.sourceforge.idyuts.IOLayer.DatagramPacketFilter;
/*     */ import net.sourceforge.idyuts.IOLayer.StreamFilter;
/*     */ 
/*     */ public final class NotificationFactory
/*     */   implements Runnable, DatagramPacketFilter, StreamFilter
/*     */ {
/*  16 */   private Set recv = new HashSet();
/*  17 */   private ReferenceQueue q = new ReferenceQueue();
/*     */   private DatagramSocket ds;
/*     */ 
/*     */   public final void recv(InputStream reader)
/*     */   {
/*     */     try
/*     */     {
/*  22 */       MetaProperties n = new Notification();
/*  23 */       n.load(reader);
/*  24 */       boolean more = ackPacket(n);
/*  25 */       if (more)
/*  26 */         routePacket(n);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  30 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final boolean ackPacket(MetaProperties n) throws IOException, SocketException {
/*  35 */     String s = (String)n.get("ACK");
/*     */ 
/*  37 */     if (s != null) {
/*  38 */       Env.log(13, "NotificationFactory.handleStream() ACK Notification: " + s);
/*  39 */       Env.getowchDispatch().remove(s);
/*  40 */       return false;
/*     */     }
/*     */ 
/*  43 */     if (n.getJMSReplyTo() == null) {
/*  44 */       throw new IOException("NotificationFactory has been sent a deformed Notification.");
/*     */     }
/*  46 */     s = (String)n.get("JMSMessageID");
/*  47 */     if (s != null) {
/*  48 */       URLString url = new URLString(n.getURL());
/*  49 */       MetaProperties n2 = new Notification();
/*  50 */       n2.put("ACK", s);
/*  51 */       String h = url.getHost();
/*  52 */       InetAddress dest = InetAddress.getByName(h);
/*  53 */       OutputStream os = new ByteArrayOutputStream();
/*  54 */       n2.save(os);
/*  55 */       byte[] buf = os.toString().getBytes();
/*     */ 
/*  57 */       DatagramPacket p = new DatagramPacket(buf, buf.length, dest, url.getPort());
/*     */ 
/*  60 */       ds.send(p);
/*  61 */       return true;
/*     */     }
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   public final void routePacket(MetaProperties n) {
/*  67 */     String s = (String)n.get("JMSMessageID");
/*  68 */     if (!recognize(n, s))
/*  69 */       Env.send(n);
/*     */   }
/*     */ 
/*     */   public boolean recognize(MetaProperties n, String s)
/*     */   {
/*  75 */     boolean res = false;
/*     */ 
/*  77 */     synchronized (recv) {
/*  78 */       Iterator i = recv.iterator();
/*  79 */       while (i.hasNext()) {
/*  80 */         SoftReference ref = (SoftReference)i.next();
/*  81 */         String prev = (String)ref.get();
/*  82 */         if (prev == null) {
/*     */           continue;
/*     */         }
/*  85 */         if (prev.equals(s)) {
/*  86 */           res = true;
/*  87 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  94 */     return res;
/*     */   }
/*     */ 
/*     */   public NotificationFactory() throws SocketException
/*     */   {
/*  99 */     ds = new DatagramSocket(0);
/* 100 */     Thread t = new Thread();
/* 101 */     t.setDaemon(true);
/* 102 */     t.start();
/*     */   }
/*     */ 
/*     */   public void recv(DatagramPacket p) {
/* 106 */     ByteArrayInputStream istream = new ByteArrayInputStream(p.getData());
/*     */     try {
/* 108 */       recv(istream);
/* 109 */       istream.close();
/*     */     }
/*     */     catch (IOException e) {
/* 112 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/* 120 */       while (!Env.shutdown) {
/* 121 */         Reference ref = q.remove(3000L);
/* 122 */         if (ref != null) {
/* 123 */           synchronized (recv) {
/* 124 */             recv.remove(ref);
/* 125 */             Env.log(40, getClass().getName() + "::collecting softref ---- ");
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 132 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.NotificationFactory
 * JD-Core Version:    0.6.0
 */