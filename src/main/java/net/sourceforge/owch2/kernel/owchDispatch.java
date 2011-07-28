/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.DatagramPacket;
/*    */ import java.util.Enumeration;
/*    */ import java.util.Hashtable;
/*    */ 
/*    */ public final class owchDispatch
/*    */   implements Runnable, BehaviorState
/*    */ {
/* 14 */   Hashtable pending = new Hashtable(2, 1.0F);
/* 15 */   Hashtable tenacious = new Hashtable(2, 1.0F);
/*    */ 
/*    */   public void handleDatagram(String serr, DatagramPacket p, boolean priority) {
/* 18 */     dpwrap dpw = new dpwrap(p);
/*    */     Hashtable ht;
/* 20 */     if (priority) {
/* 21 */       ht = pending;
/*    */     }
/*    */     else {
/* 24 */       ht = tenacious;
/*    */     }
/* 26 */     ht.put(serr, dpw);
/*    */     try {
/* 28 */       Env.log(18, "debug: ht.put(serr,p)");
/* 29 */       dpw.fire();
/*    */     }
/*    */     catch (IOException e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   void remove(String serr)
/*    */   {
/* 38 */     Env.log(18, "debug: remove " + serr.toString());
/* 39 */     tenacious.remove(serr);
/* 40 */     pending.remove(serr);
/*    */   }
/*    */ 
/*    */   owchDispatch() {
/*    */     try {
/* 45 */       Thread t = new Thread(this, "SocketCache");
/* 46 */       t.setDaemon(true);
/* 47 */       t.start();
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/*    */     }
/*    */   }
/*    */ 
/*    */   public void run() {
/* 55 */     int count = 0;
/* 56 */     while (!Env.shutdown) {
/*    */       try {
/* 58 */         Thread.currentThread(); Thread.sleep(1800L);
/*    */       }
/*    */       catch (InterruptedException ex) {
/* 61 */         Env.log(18, "debug: owchDispatch.run() e " + ex);
/*    */       }
/*    */ 
/* 64 */       Enumeration en = pending.keys();
/* 65 */       scatter(en, false);
/* 66 */       en = tenacious.keys();
/* 67 */       scatter(en, true);
/*    */     }
/*    */   }
/*    */ 
/*    */   private final void scatter(Enumeration e, boolean priority)
/*    */   {
/* 80 */     while (e.hasMoreElements())
/*    */       try
/*    */       {
/*    */         Hashtable ht;
/* 82 */         if (priority) {
/* 83 */           ht = tenacious;
/*    */         }
/*    */         else {
/* 86 */           ht = pending;
/*    */         }
/* 88 */         String serr = (String)e.nextElement();
/* 89 */         dpwrap dpw = (dpwrap)ht.get(serr);
/* 90 */         if (dpw != null) {
/* 91 */           byte st = dpw.fire();
/*    */ 
/* 93 */           if (st != 2) Env.log(18, "debug: owchDispatch.run() send " + BehaviorState.age[st] + " " + serr);
/* 94 */           if (st == 3)
/* 95 */             remove(serr);
/*    */         }
/*    */       }
/*    */       catch (IOException ex)
/*    */       {
/* 100 */         Env.log(18, "debug: owchDispatch.run() e " + ex);
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.owchDispatch
 * JD-Core Version:    0.6.0
 */