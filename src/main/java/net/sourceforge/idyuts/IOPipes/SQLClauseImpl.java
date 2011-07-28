/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.ArrayFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.StringFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.StringSource;
/*    */ 
/*    */ public abstract class SQLClauseImpl
/*    */   implements ArrayFilter, StringSource
/*    */ {
/*    */   protected List clients;
/*    */   protected String stage;
/* 21 */   public static final Class[][] String_sources = { { String.class } };
/*    */ 
/* 65 */   public static final Class[][] Array_filters = { { new Object[0].getClass() } };
/*    */ 
/*    */   public SQLClauseImpl()
/*    */   {
/*  9 */     clients = new ArrayList(1);
/*    */   }
/*    */ 
/*    */   public void attach(StringFilter filter) {
/* 13 */     clients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(StringFilter filter) {
/* 17 */     clients.remove(filter);
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 26 */     return String_sources;
/*    */   }
/*    */ 
/*    */   public void xmit() {
/*    */     try {
/* 31 */       for (int ci = 0; ci < clients.size(); ci++) {
/* 32 */         StringFilter filter = (StringFilter)clients.get(ci);
/* 33 */         filter.recv(stage);
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 37 */       throw new Error("more debugging needed here");
/*    */     }
/*    */   }
/*    */ 
/*    */   public void recv(Object[] data)
/*    */   {
/*    */   }
/*    */ 
/*    */   protected String f_loop(Object[] s, String pref, String sep)
/*    */   {
/* 49 */     String sql = "";
/* 50 */     for (int ti = 0; ti < s.length; ti++) {
/* 51 */       String t = "" + s[ti];
/*    */       String a;
/* 52 */       if (ti == 0) {
/* 53 */         a = pref;
/*    */       }
/*    */       else {
/* 56 */         a = sep;
/*    */       }
/* 58 */       sql = sql + a + t;
/*    */     }
/*    */ 
/* 61 */     return sql;
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 70 */     return Array_filters;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.SQLClauseImpl
 * JD-Core Version:    0.6.0
 */