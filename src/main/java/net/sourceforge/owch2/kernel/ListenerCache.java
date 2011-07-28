/*     */ package net.sourceforge.owch2.kernel;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class ListenerCache
/*     */   implements Runnable
/*     */ {
/*  11 */   Map cache = new TreeMap();
/*     */ 
/*  13 */   Iterator enumCycle = null;
/*     */   boolean enumFlag;
/*  69 */   long lowscore = 0L;
/*  70 */   ListenerReference nextInLine = null;
/*     */ 
/*     */   public Location getLocation()
/*     */   {
/*  18 */     ListenerReference lr = getNextInLine();
/*     */ 
/*  20 */     return Location.create(lr);
/*     */   }
/*     */ 
/*     */   public ListenerReference getNextInLine()
/*     */   {
/*  26 */     if (!enumFlag) {
/*  27 */       enumCycle = cache.keySet().iterator();
/*  28 */       enumFlag = true;
/*     */     }
/*     */ 
/*  31 */     if (enumCycle.hasNext()) {
/*  32 */       return (ListenerReference)cache.get(enumCycle.next());
/*     */     }
/*     */ 
/*  36 */     if (cache.size() == 0) {
/*  37 */       return null;
/*     */     }
/*     */ 
/*  41 */     enumFlag = false;
/*     */ 
/*  43 */     return getNextInLine();
/*     */   }
/*     */ 
/*     */   public void put(ListenerReference l) {
/*  47 */     cache.put(new Integer(l.getServer().getLocalPort()), l);
/*  48 */     if (l.getExpiration() < lowscore) {
/*  49 */       resetExpire();
/*     */     }
/*  51 */     enumFlag = false;
/*     */   }
/*     */ 
/*     */   public ListenerReference remove(int port) {
/*  55 */     ListenerReference l = (ListenerReference)cache.remove(new Integer(port));
/*  56 */     if (l == nextInLine) {
/*  57 */       resetExpire();
/*     */     }
/*  59 */     enumFlag = false;
/*  60 */     return l;
/*     */   }
/*     */ 
/*     */   public ListenerCache() {
/*  64 */     Thread t = new Thread(this, "ListenerCache");
/*  65 */     t.setDaemon(true);
/*  66 */     t.start();
/*     */   }
/*     */ 
/*     */   public synchronized void resetExpire()
/*     */   {
/*  73 */     lowscore = 0L;
/*  74 */     nextInLine = null;
/*     */ 
/*  76 */     for (Iterator e = cache.keySet().iterator(); e.hasNext(); ) {
/*  77 */       ListenerReference l = (ListenerReference)cache.get(e.next());
/*  78 */       if (l.getExpiration() == 0L) {
/*     */         continue;
/*     */       }
/*  81 */       if (lowscore == 0L) {
/*  82 */         lowscore = (l.getExpiration() + 60000L);
/*     */       }
/*     */ 
/*  86 */       if (l.getExpiration() < lowscore) {
/*  87 */         nextInLine = l;
/*  88 */         lowscore = l.getExpiration();
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*  97 */     notify();
/*     */   }
/*     */ 
/*     */   public synchronized void run() {
/* 101 */     while (!Env.shutdown)
/*     */       try {
/* 103 */         if (lowscore == 0L) {
/* 104 */           wait(5000L);
/*     */         }
/*     */         else
/*     */         {
/* 108 */           wait(lowscore - System.currentTimeMillis());
/*     */         }
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.ListenerCache
 * JD-Core Version:    0.6.0
 */