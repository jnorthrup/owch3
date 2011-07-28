/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.ArrayFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.ArraySource;
/*    */ import net.sourceforge.idyuts.IOLayer.TableFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.intFilter;
/*    */ 
/*    */ class ColumnValues
/*    */   implements ArraySource, TableFilter, intFilter
/*    */ {
/*  8 */   int column = 0;
/*    */   List list;
/*    */   Object[][] data;
/* 41 */   public static final Class[][] Table_filters = { { new Object[0].getClass() }, { Integer.TYPE } };
/*    */ 
/* 48 */   private List _Arrayclients = new ArrayList(1);
/*    */ 
/* 59 */   public static final Class[][] Array_sources = { { new Object[0].getClass() }, { Boolean.class } };
/*    */ 
/*    */   public void recv(int data)
/*    */   {
/* 11 */     column = data;
/* 12 */     xmit();
/*    */   }
/*    */ 
/*    */   ColumnValues(int column) {
/* 16 */     this.column = column;
/*    */   }
/*    */ 
/*    */   ColumnValues() {
/*    */   }
/*    */ 
/*    */   public void collect() {
/* 23 */     list = new ArrayList(1);
/* 24 */     for (int i = 0; i < data.length; i++)
/* 25 */       list.add(data[i][column]);
/*    */   }
/*    */ 
/*    */   public void recv(Object[][] data)
/*    */   {
/* 34 */     synchronized (data) {
/* 35 */       this.data = data;
/* 36 */       xmit();
/*    */     }
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 45 */     return Table_filters;
/*    */   }
/*    */ 
/*    */   public void attach(ArrayFilter filter)
/*    */   {
/* 51 */     _Arrayclients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(ArrayFilter filter) {
/* 55 */     _Arrayclients.remove(filter);
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 64 */     return Array_sources;
/*    */   }
/*    */ 
/*    */   public synchronized void xmit() {
/*    */     try {
/* 69 */       if (data == null) {
/* 70 */         return;
/*    */       }
/* 72 */       collect();
/* 73 */       Object[] arr = list.toArray();
/* 74 */       for (int ci = 0; ci < _Arrayclients.size(); ci++) {
/* 75 */         ArrayFilter filter = (ArrayFilter)_Arrayclients.get(ci);
/* 76 */         filter.recv(arr);
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 80 */       throw new Error("more debugging needed here");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.ColumnValues
 * JD-Core Version:    0.6.0
 */