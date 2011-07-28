/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class ProtocolCache extends HashMap
/*    */ {
/*    */   public ListenerCache getListenerCache(String Protocol)
/*    */   {
/* 15 */     Env.log(20, "protocolCache.getListenerCache -- " + Protocol);
/* 16 */     ListenerCache lc = (ListenerCache)get(Protocol);
/* 17 */     if (lc == null) {
/*    */       try {
/* 19 */         Env.log(20, "attempting to create " + Protocol);
/* 20 */         String cname = "owch." + Protocol;
/* 21 */         String factory = Protocol + "Factory";
/* 22 */         Env.log(20, "attempting to register " + factory);
/* 23 */         lc = new ListenerCache();
/* 24 */         for (int i = 0; i < Env.getSocketCount(); i++) {
/* 25 */           Method m1 = Env.class.getMethod("get" + factory, new Class[0]);
/*    */ 
/* 27 */           ListenerFactory lf = (ListenerFactory)m1.invoke(this, new Object[0]);
/*    */ 
/* 29 */           lc.put(lf.create(Env.getHostAddress(), i == 0 ? Env.getHostPort() : 0, Env.getHostThreads()));
/*    */         }
/* 31 */         put(Protocol, lc);
/*    */       }
/*    */       catch (Exception e) {
/* 34 */         e.printStackTrace();
/*    */       }
/*    */     }
/* 37 */     return lc;
/*    */   }
/*    */ 
/*    */   public Location getLocation(String Protocol) {
/* 41 */     ListenerCache l = getListenerCache(Protocol);
/*    */ 
/* 43 */     if (l != null) {
/* 44 */       return l.getLocation();
/*    */     }
/*    */ 
/* 47 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.ProtocolCache
 * JD-Core Version:    0.6.0
 */