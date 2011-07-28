/*     */ package net.sourceforge.owch2.kernel;
/*     */ 
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.SoftReference;
/*     */ import java.net.Socket;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import java.util.TreeSet;
/*     */ import java.util.WeakHashMap;
/*     */ import net.sourceforge.owch2.router.Router;
/*     */ 
/*     */ public class httpRegistry extends Registry
/*     */ {
/*     */   public httpRegistry()
/*     */   {
/*  18 */     int a = 4;
/*     */ 
/*  20 */     setWeakMap(new WeakHashMap(384));
/*  21 */     setComparator(new URLComparator());
/*  22 */     setSet(new TreeSet(getComparator()));
/*     */   }
/*     */ 
/*     */   public String displayKey(Comparable key) {
/*  26 */     return key.toString();
/*     */   }
/*     */ 
/*     */   public String displayValue(Reference reference) {
/*  30 */     Map map = (Map)reference.get();
/*  31 */     if (map == null) {
/*  32 */       return "*something utterly unimportant*";
/*     */     }
/*     */ 
/*  35 */     return map.get("JMSReplyTo").toString();
/*     */   }
/*     */ 
/*     */   public Reference referenceValue(Object o)
/*     */   {
/*  40 */     return new SoftReference(o, getRefQ());
/*     */   }
/*     */ 
/*     */   public boolean dispatchRequest(Socket socket, MetaProperties notification)
/*     */   {
/*  67 */     String resource = notification.get("Resource").toString();
/*  68 */     String method = notification.get("Method").toString();
/*  69 */     notification.put("Proxy-Request", notification.get("Request"));
/*     */ 
/*  73 */     if (isCacheInvalid()) {
/*  74 */       reCache();
/*     */     }
/*     */ 
/*  77 */     MetaAgent l = (MetaAgent)weakGet(resource);
/*  78 */     if (l == null) {
/*  79 */       int len = resource.length();
/*  80 */       for (int i = getCache().length - 1; i >= 0; i--) {
/*  81 */         String temp = getCache(i).toString();
/*  82 */         Env.log(500, "Pattern test on " + resource + ":" + temp);
/*  83 */         if (temp.length() > len) {
/*     */           continue;
/*     */         }
/*  86 */         if (resource.startsWith(temp)) {
/*  87 */           l = (MetaAgent)weakGet(temp);
/*  88 */           Env.log(500, "Pattern match on " + resource + ":" + temp);
/*     */         }
/*     */       }
/*     */     }
/*  92 */     if (l != null) {
/*  93 */       String lname = l.getJMSReplyTo();
/*     */ 
/*  96 */       if (Env.getRouter("IPC").hasElement(lname))
/*     */       {
/* 101 */         notification.put("_Socket", socket);
/* 102 */         notification.put("JMSDestination", lname);
/* 103 */         notification.put("JMSType", "httpd");
/* 104 */         notification.put("JMSReplyTo", "nobody");
/*     */ 
/* 106 */         Env.send(notification);
/* 107 */         return true;
/*     */       }
/*     */ 
/* 110 */       Env.log(15, getClass().getName() + " creating PipeSocket for " + notification.get("Resource").toString());
/* 111 */       PipeSocket pipeSocket = new httpPipeSocket(socket, l, notification);
/* 112 */       return true;
/*     */     }
/*     */ 
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */   class URLComparator
/*     */     implements Comparator
/*     */   {
/*     */     URLComparator()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int compare(Object o1, Object o2)
/*     */     {
/*  46 */       int res = o1.toString().length() - o2.toString().length();
/*  47 */       if (res == 0) {
/*  48 */         res = o1.toString().compareTo(o2.toString());
/*     */       }
/*  50 */       return res;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj)
/*     */     {
/*  55 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.httpRegistry
 * JD-Core Version:    0.6.0
 */