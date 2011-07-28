/*    */ package net.sourceforge.nlp;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ 
/*    */ public class Occurence
/*    */   implements Serializable
/*    */ {
/*  6 */   private int count = 1;
/*    */ 
/*    */   public final void incr()
/*    */   {
/* 12 */     count += 1;
/*    */   }
/*    */ 
/*    */   public int getCount() {
/* 16 */     return count;
/*    */   }
/*    */ 
/*    */   public void setCount(int count) {
/* 20 */     this.count = count;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.nlp.Occurence
 * JD-Core Version:    0.6.0
 */