/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.StringFilter;
/*    */ 
/*    */ public abstract class StringFunctorImpl
/*    */   implements StringFunctor
/*    */ {
/*    */   private List clients;
/*    */   private String stage;
/* 20 */   public static final Class[][] _String_filters = { { String.class } };
/*    */   private List _String_clients;
/* 41 */   private static Class[] foo = { new Object[0].getClass() };
/*    */ 
/* 61 */   public static final Class[][] _String_sources = { { String.class } };
/*    */ 
/*    */   public StringFunctorImpl()
/*    */   {
/* 16 */     clients = new ArrayList(1);
/*    */ 
/* 31 */     _String_clients = new ArrayList(1);
/*    */   }
/*    */ 
/*    */   public void recv(String data)
/*    */   {
/*  9 */     synchronized (this) {
/* 10 */       stage = fire(data);
/* 11 */       xmit();
/*    */     }
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 26 */     return _String_filters;
/*    */   }
/*    */ 
/*    */   public void attach(StringFilter filter)
/*    */   {
/* 34 */     _String_clients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(StringFilter filter) {
/* 38 */     _String_clients.remove(filter);
/*    */   }
/*    */ 
/*    */   public void xmit()
/*    */   {
/*    */     try
/*    */     {
/* 46 */       Object data = null;
/* 47 */       for (int ci = 0; ci < _String_clients.size(); ) {
/* 48 */         StringFilter filter = (StringFilter)_String_clients.get(ci);
/* 49 */         filter.getClass().getMethod("recv", foo).invoke(filter, new Object[] { data });
/*    */ 
/* 52 */         throw new Error("unfinished code");
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 56 */       throw new Error("more debugging needed here");
/*    */     }
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 68 */     return _String_sources;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.StringFunctorImpl
 * JD-Core Version:    0.6.0
 */