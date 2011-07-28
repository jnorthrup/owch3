/*    */ package net.sourceforge.idyuts.IOPipes.functor;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.StringTokenizer;
/*    */ import net.sourceforge.idyuts.IOPipes.StringFunctorImpl;
/*    */ 
/*    */ public class xlateBetween extends StringFunctorImpl
/*    */ {
/*    */   public String fire(String s)
/*    */   {
/* 10 */     if (!s.startsWith("~BETWEENDATES")) {
/* 11 */       return s;
/*    */     }
/* 13 */     List arr = new ArrayList(2);
/*    */ 
/* 15 */     StringTokenizer st = new StringTokenizer(s.substring("~BETWEENDATES".length(), 44));
/* 16 */     while (st.hasMoreTokens()) {
/* 17 */       arr.add(st.nextToken());
/*    */     }
/* 19 */     String t = " BETWEEN ";
/* 20 */     for (int ci = 0; ci < arr.size(); ci++) {
/* 21 */       String ii = (String)arr.get(ci);
/* 22 */       if (ci > 0) {
/* 23 */         t = t + " AND ";
/*    */       }
/* 25 */       t = t + " TO_DATE('" + ii.trim() + "','YYYY-MMM-DD')";
/*    */     }
/*    */ 
/* 28 */     return t;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.functor.xlateBetween
 * JD-Core Version:    0.6.0
 */