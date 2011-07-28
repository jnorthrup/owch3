/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ public class Log
/*    */ {
/*    */   public static final boolean logDebug = true;
/* 13 */   protected static int debugLevel = 6000;
/* 14 */   private static DebugTimerOutputStream debugTimerOutputStream = new DebugTimerOutputStream(System.out);
/*    */ 
/*    */   public static DebugTimerOutputStream getDebugStream() {
/* 17 */     return debugTimerOutputStream;
/*    */   }
/*    */ 
/*    */   public static void log(int lev, String s)
/*    */   {
/* 26 */     if (debugLevel >= lev)
/* 27 */       debugTimerOutputStream.println(s);
/*    */   }
/*    */ 
/*    */   public static void setDebugLevel(int i)
/*    */   {
/* 36 */     debugLevel = i;
/*    */   }
/*    */ 
/*    */   public static int getDebugLevel()
/*    */   {
/* 44 */     return debugLevel;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.Log
 * JD-Core Version:    0.6.0
 */