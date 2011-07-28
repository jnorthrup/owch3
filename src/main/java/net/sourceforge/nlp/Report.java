/*    */ package net.sourceforge.nlp;
/*    */ 
/*    */ public class Report
/*    */ {
/*    */   private Occurence occ;
/*    */   private String text;
/*    */ 
/*    */   public Report(String t, Occurence o)
/*    */   {
/* 10 */     occ = o;
/* 11 */     text = t;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 15 */     return text + "=" + occ.getCount();
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.nlp.Report
 * JD-Core Version:    0.6.0
 */