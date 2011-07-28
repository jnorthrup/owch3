/*    */ package net.sourceforge.gui;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import net.sourceforge.gui.IRC.IRCVisitor;
/*    */ import net.sourceforge.owch2.kernel.Env;
/*    */ 
/*    */ public class AgentGUI
/*    */ {
/* 10 */   private AgentVisitor gui = new IRCVisitor();
/*    */ 
/*    */   public static void main(String[] args)
/*    */     throws Exception
/*    */   {
/* 18 */     Map map = Env.parseCommandLineArgs(args);
/* 19 */     AgentGUI f = new AgentGUI();
/* 20 */     Iterator i = map.keySet().iterator();
/* 21 */     while (i.hasNext()) {
/* 22 */       Object key = i.next();
/* 23 */       f.getGui().put(key, pair(map, key));
/*    */     }
/* 25 */     Thread.currentThread(); Thread.sleep(10000L);
/*    */   }
/*    */ 
/*    */   private static Object pair(Map map, Object key) {
/* 29 */     return map.get(key);
/*    */   }
/*    */ 
/*    */   public AgentVisitor getGui() {
/* 33 */     return gui;
/*    */   }
/*    */ 
/*    */   public void setGui(AgentVisitor gui) {
/* 37 */     this.gui = gui;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.gui.AgentGUI
 * JD-Core Version:    0.6.0
 */