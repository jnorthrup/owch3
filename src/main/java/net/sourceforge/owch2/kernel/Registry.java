/*     */ package net.sourceforge.owch2.kernel;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import java.util.SortedSet;
/*     */ 
/*     */ public abstract class Registry
/*     */ {
/*     */   private Object[] cache;
/*     */   private ReferenceQueue refQ;
/*     */   private Comparator comparator;
/*     */   private boolean cacheInvalid;
/*     */   private Map weakMap;
/*     */   private SortedSet set;
/*     */ 
/*     */   public Registry()
/*     */   {
/*  39 */     refQ = new ReferenceQueue();
/*     */ 
/*  43 */     cacheInvalid = true;
/*     */   }
/*     */ 
/*     */   protected Reference refGet(Object key)
/*     */   {
/*  16 */     return (Reference)getWeakMap().get(key);
/*     */   }
/*     */ 
/*     */   protected Object weakGet(Object key) {
/*  20 */     if (key == null) {
/*  21 */       return null;
/*     */     }
/*  23 */     Reference r = refGet(key);
/*  24 */     if (r == null) {
/*  25 */       return null;
/*     */     }
/*  27 */     Object o = r.get();
/*  28 */     return o;
/*     */   }
/*     */ 
/*     */   public abstract String displayKey(Comparable paramComparable);
/*     */ 
/*     */   public abstract String displayValue(Reference paramReference);
/*     */ 
/*     */   public abstract Reference referenceValue(Object paramObject);
/*     */ 
/*     */   public void registerItem(Comparable key, Object val)
/*     */   {
/*  50 */     registerItem(key, referenceValue(val));
/*     */   }
/*     */ 
/*     */   protected void registerItem(Comparable key, Reference val) {
/*  54 */     synchronized (getSet()) {
/*  55 */       getSet().add(key);
/*  56 */       setCacheInvalid(true);
/*     */     }
/*     */ 
/*  59 */     getWeakMap().put(key, val);
/*     */ 
/*  61 */     Env.log(15, getClass().getName() + ":::Item Registration:" + "@" + displayKey(key) + " -- " + displayValue(val));
/*     */   }
/*     */ 
/*     */   public void unregisterItem(Comparable key)
/*     */   {
/*  67 */     synchronized (getSet()) {
/*  68 */       getSet().remove(key);
/*  69 */       setCacheInvalid(true);
/*     */     }
/*     */ 
/*  72 */     Env.log(15, getClass().getName() + ":::Item DeRegistration:" + displayKey(key));
/*     */   }
/*     */ 
/*     */   protected void reCache()
/*     */   {
/*  77 */     Env.log(150, getClass().getName() + " recache starting..");
/*  78 */     synchronized (getSet()) {
/*  79 */       setCache(getSet().toArray());
/*  80 */       setCacheInvalid(false);
/*  81 */       Env.log(150, getClass().getName() + " recache done..");
/*     */     }
/*     */ 
/*  84 */     Env.log(150, getClass().getName() + " - recache fin..");
/*     */   }
/*     */ 
/*     */   public Object[] getCache() {
/*  88 */     return cache;
/*     */   }
/*     */ 
/*     */   public void setCache(Object[] cache) {
/*  92 */     this.cache = cache;
/*     */   }
/*     */ 
/*     */   public Object getCache(int index) {
/*  96 */     return cache[index];
/*     */   }
/*     */ 
/*     */   public void setCache(int i, Object cache) {
/* 100 */     this.cache[i] = cache;
/*     */   }
/*     */ 
/*     */   public ReferenceQueue getRefQ() {
/* 104 */     return refQ;
/*     */   }
/*     */ 
/*     */   public void setRefQ(ReferenceQueue refQ) {
/* 108 */     this.refQ = refQ;
/*     */   }
/*     */ 
/*     */   public Comparator getComparator() {
/* 112 */     return comparator;
/*     */   }
/*     */ 
/*     */   public void setComparator(Comparator comparator) {
/* 116 */     this.comparator = comparator;
/*     */   }
/*     */ 
/*     */   public boolean isCacheInvalid() {
/* 120 */     return cacheInvalid;
/*     */   }
/*     */ 
/*     */   public void setCacheInvalid(boolean cacheInvalid) {
/* 124 */     this.cacheInvalid = cacheInvalid;
/*     */   }
/*     */ 
/*     */   public Map getWeakMap() {
/* 128 */     return weakMap;
/*     */   }
/*     */ 
/*     */   public void setWeakMap(Map weakMap) {
/* 132 */     this.weakMap = weakMap;
/*     */   }
/*     */ 
/*     */   public SortedSet getSet() {
/* 136 */     return set;
/*     */   }
/*     */ 
/*     */   public void setSet(SortedSet set) {
/* 140 */     this.set = set;
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.Registry
 * JD-Core Version:    0.6.0
 */