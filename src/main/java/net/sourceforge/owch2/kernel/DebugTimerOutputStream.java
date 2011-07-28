/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ 
/*    */ public class DebugTimerOutputStream extends PrintStream
/*    */ {
/* 10 */   private static long benchmark = System.currentTimeMillis();
/*    */   private static long time;
/*    */   private static long diff;
/*    */ 
/*    */   DebugTimerOutputStream(PrintStream os)
/*    */   {
/* 15 */     super(os);
/*    */   }
/*    */ 
/*    */   public void println(String s) {
/* 19 */     time = System.currentTimeMillis();
/* 20 */     diff = time - benchmark;
/* 21 */     super.println(time + "\t " + diff + "\t " + s);
/* 22 */     benchmark = time;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.DebugTimerOutputStream
 * JD-Core Version:    0.6.0
 */