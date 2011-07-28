/*   */ package net.sourceforge.idyuts.IOPipes.SQLToken;
/*   */ 
/*   */ import net.sourceforge.idyuts.IOPipes.SQLClauseImpl;
/*   */ 
/*   */ public class SELECT extends SQLClauseImpl
/*   */ {
/*   */   public void recv(Object[] s)
/*   */   {
/* 9 */     synchronized (stage) {
/* 10 */       stage = f_loop(s, "SELECT ", ", ");
/* 11 */       xmit();
/*   */     }
/*   */   }
/*   */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.SQLToken.SELECT
 * JD-Core Version:    0.6.0
 */