/*    */ package net.sourceforge.idyuts.test;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.util.Collection;
/*    */ import net.sourceforge.idyuts.IOLayer.CollectionFilter;
/*    */ 
/*    */ public class CollectionPrinter
/*    */   implements CollectionFilter
/*    */ {
/* 17 */   public static final Class[][] _Collection_filters = { { Collection.class } };
/*    */ 
/*    */   public void recv(Collection data)
/*    */   {
/* 12 */     System.out.println(getClass().getName() + "recv" + data);
/* 13 */     System.out.println(data.toString());
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 24 */     return _Collection_filters;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.test.CollectionPrinter
 * JD-Core Version:    0.6.0
 */