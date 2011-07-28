/*    */ package net.sourceforge.idyuts.IOConversion;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.ArrayFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.CollectionFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.CollectionSource;
/*    */ 
/*    */ public class ArrayCollectionConverter
/*    */   implements ArrayFilter, CollectionSource
/*    */ {
/*    */   protected Collection data;
/* 18 */   public static final Class[][] _Array_filters = { { new Object[0].getClass() } };
/*    */   private List _Collection_clients;
/* 55 */   public static final Class[][] _Collection_sources = { { Collection.class } };
/*    */ 
/*    */   public ArrayCollectionConverter()
/*    */   {
/* 30 */     _Collection_clients = new ArrayList(1);
/*    */   }
/*    */ 
/*    */   public void recv(Object[] data)
/*    */   {
/* 12 */     System.out.println(getClass().getName() + "recv" + data);
/* 13 */     this.data = Arrays.asList(data);
/* 14 */     xmit();
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 25 */     return _Array_filters;
/*    */   }
/*    */ 
/*    */   public void attach(CollectionFilter filter)
/*    */   {
/* 33 */     _Collection_clients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(CollectionFilter filter) {
/* 37 */     _Collection_clients.remove(filter);
/*    */   }
/*    */ 
/*    */   public void xmit()
/*    */   {
/*    */     try {
/* 43 */       for (int ci = 0; ci < _Collection_clients.size(); ci++) {
/* 44 */         CollectionFilter filter = (CollectionFilter)_Collection_clients.get(ci);
/* 45 */         filter.recv(data);
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 49 */       e.printStackTrace();
/* 50 */       throw new Error("more debugging needed here");
/*    */     }
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 63 */     return _Collection_sources;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOConversion.ArrayCollectionConverter
 * JD-Core Version:    0.6.0
 */