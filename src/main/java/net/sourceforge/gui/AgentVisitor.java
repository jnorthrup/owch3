/*    */ package net.sourceforge.gui;
/*    */ 
/*    */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*    */ 
/*    */ public abstract interface AgentVisitor
/*    */ {
/* 26 */   public static final String default_val = "default".intern();
/* 27 */   public static final Object[] no_Parm = new Object[0];
/* 28 */   public static final Class[] no_class = new Class[0];
/*    */ 
/*    */   public abstract Object get(Object paramObject);
/*    */ 
/*    */   public abstract void put(Object paramObject1, Object paramObject2);
/*    */ 
/*    */   public abstract void stopAgent();
/*    */ 
/*    */   public abstract AbstractAgent getNode();
/*    */ 
/*    */   public abstract void startAgent();
/*    */ 
/*    */   public abstract void initGUI();
/*    */ 
/*    */   public abstract String[] getApp_keys();
/*    */ 
/*    */   public abstract String getApp_keys(int paramInt);
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.gui.AgentVisitor
 * JD-Core Version:    0.6.0
 */