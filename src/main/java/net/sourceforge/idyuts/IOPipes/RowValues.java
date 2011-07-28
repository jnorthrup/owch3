/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.ArrayFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.ArraySource;
/*    */ import net.sourceforge.idyuts.IOLayer.TableFilter;
/*    */ 
/*    */ public class RowValues
/*    */   implements TableFilter, ArraySource
/*    */ {
/*    */   private List _Array_clients;
/* 19 */   private static Class[] foo = { new Object[0].getClass() };
/*    */ 
/* 39 */   public static final Class[][] _Array_sources = { { new Object[0].getClass() } };
/*    */ 
/* 58 */   public static final Class[][] _Table_filters = { { new Object[0].getClass() } };
/*    */ 
/*    */   public RowValues()
/*    */   {
/*  9 */     _Array_clients = new ArrayList(1);
/*    */   }
/*    */   public void attach(ArrayFilter filter) {
/* 12 */     _Array_clients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(ArrayFilter filter) {
/* 16 */     _Array_clients.remove(filter);
/*    */   }
/*    */ 
/*    */   public void xmit()
/*    */   {
/*    */     try
/*    */     {
/* 24 */       Object data = null;
/* 25 */       for (int ci = 0; ci < _Array_clients.size(); ) {
/* 26 */         ArrayFilter filter = (ArrayFilter)_Array_clients.get(ci);
/* 27 */         filter.getClass().getMethod("recv", foo).invoke(filter, new Object[] { data });
/*    */ 
/* 30 */         throw new Error("unfinished code");
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 34 */       throw new Error("more debugging needed here");
/*    */     }
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 46 */     return _Array_sources;
/*    */   }
/*    */ 
/*    */   public void recv(Object[][] data)
/*    */   {
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 65 */     return _Table_filters;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.RowValues
 * JD-Core Version:    0.6.0
 */