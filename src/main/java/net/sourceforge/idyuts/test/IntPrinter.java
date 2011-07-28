/*    */ package net.sourceforge.idyuts.test;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import net.sourceforge.idyuts.IOLayer.intFilter;
/*    */ 
/*    */ public class IntPrinter
/*    */   implements intFilter
/*    */ {
/* 17 */   public static final Class[][] _Int_filters = { { Integer.TYPE } };
/*    */ 
/*    */   public void recv(int evt)
/*    */   {
/* 13 */     System.out.println("" + evt);
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 23 */     return _Int_filters;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.test.IntPrinter
 * JD-Core Version:    0.6.0
 */