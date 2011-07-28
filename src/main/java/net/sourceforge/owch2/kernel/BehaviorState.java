/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ public abstract interface BehaviorState
/*    */ {
/*    */   public static final int hot = 0;
/*    */   public static final int cold = 1;
/*    */   public static final int frozen = 2;
/*    */   public static final byte dead = 3;
/*    */   public static final int lifespan = 12;
/*    */   public static final int mortality = 144;
/* 10 */   public static final String[] age = { "hot", "cold", "frozen", "dead" };
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.BehaviorState
 * JD-Core Version:    0.6.0
 */