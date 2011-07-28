/*    */ package net.sourceforge.idyuts.IOPipes.functor;
/*    */ 
/*    */ import java.util.StringTokenizer;
/*    */ import net.sourceforge.idyuts.IOPipes.StringFunctorImpl;
/*    */ 
/*    */ public class xlateSecondDotToken extends StringFunctorImpl
/*    */ {
/*    */   public String fire(String s)
/*    */   {
/* 10 */     boolean t = true;
/*    */ 
/* 12 */     StringTokenizer st = new StringTokenizer(s, ".");
/* 13 */     while (st.hasMoreTokens()) {
/* 14 */       String ts = st.nextToken();
/* 15 */       if (t) {
/* 16 */         t = !t;
/*    */       }
/*    */       else {
/* 19 */         return ts;
/*    */       }
/*    */     }
/*    */ 
/* 23 */     return s;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.functor.xlateSecondDotToken
 * JD-Core Version:    0.6.0
 */