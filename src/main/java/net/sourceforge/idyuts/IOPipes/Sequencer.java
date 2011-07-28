/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import net.sourceforge.idyuts.IOLayer.intFilter;
/*    */ 
/*    */ public abstract class Sequencer
/*    */   implements intFilter
/*    */ {
/*    */   int index;
/* 14 */   public static final Class[][] Int_filters = { { Integer.TYPE } };
/*    */ 
/*    */   public Sequencer()
/*    */   {
/*  7 */     index = 0;
/*    */   }
/*    */   public void recv(int data) {
/* 10 */     index = data;
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 18 */     return Int_filters;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.Sequencer
 * JD-Core Version:    0.6.0
 */