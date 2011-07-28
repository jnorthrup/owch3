/*    */ package net.sourceforge.idyuts.IOPipes.functor;
/*    */ 
/*    */ import java.util.StringTokenizer;
/*    */ import net.sourceforge.idyuts.IOPipes.StringFunctorImpl;
/*    */ 
/*    */ public class xlateFirstDotToken extends StringFunctorImpl
/*    */ {
/*    */   public String fire(String s)
/*    */   {
/* 12 */     StringTokenizer st = new StringTokenizer(s, ".");
/* 13 */     if (st.hasMoreTokens()) {
/* 14 */       return st.nextToken();
/*    */     }
/* 16 */     return s;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.functor.xlateFirstDotToken
 * JD-Core Version:    0.6.0
 */