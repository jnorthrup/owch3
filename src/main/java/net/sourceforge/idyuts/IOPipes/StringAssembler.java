/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import net.sourceforge.idyuts.IOLayer.StringFilter;
/*    */ 
/*    */ public class StringAssembler extends Assembler
/*    */   implements StringFilter
/*    */ {
/* 17 */   public static final Class[][] _String_filters = { { String.class } };
/*    */ 
/*    */   public StringAssembler(int len)
/*    */   {
/*  7 */     super(len);
/*    */   }
/*    */ 
/*    */   public void recv(String data)
/*    */   {
/* 13 */     arr[index] = data;
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 24 */     return _String_filters;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.StringAssembler
 * JD-Core Version:    0.6.0
 */