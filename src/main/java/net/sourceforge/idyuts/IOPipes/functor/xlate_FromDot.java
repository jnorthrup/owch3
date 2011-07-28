/*    */ package net.sourceforge.idyuts.IOPipes.functor;
/*    */ 
/*    */ import net.sourceforge.idyuts.IOPipes.StringFunctorImpl;
/*    */ 
/*    */ public class xlate_FromDot extends StringFunctorImpl
/*    */ {
/*    */   public String fire(String s)
/*    */   {
/*  7 */     if (s == null) {
/*  8 */       s = "";
/*    */     }
/* 10 */     return s.replace('.', '_');
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.functor.xlate_FromDot
 * JD-Core Version:    0.6.0
 */