/*   */ package net.sourceforge.idyuts.IOPipes.SQLToken;
/*   */ 
/*   */ import net.sourceforge.idyuts.IOPipes.SQLClauseImpl;
/*   */ 
/*   */ public class FROM extends SQLClauseImpl
/*   */ {
/*   */   public void recv(Object[] s)
/*   */   {
/* 9 */     synchronized (stage) {
/* 10 */       stage = f_loop(s, " FROM ", ", ");
/* 11 */       xmit();
/*   */     }
/*   */   }
/*   */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.SQLToken.FROM
 * JD-Core Version:    0.6.0
 */