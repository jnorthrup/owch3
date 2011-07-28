/*    */ package net.sourceforge.owch2.router;
/*    */ 
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class nullRouter
/*    */   implements Router
/*    */ {
/*    */   public boolean hasElement(Object key)
/*    */   {
/* 12 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean addElement(Map item) {
/* 16 */     return true;
/*    */   }
/*    */ 
/*    */   public void remove(Object key) {
/*    */   }
/*    */ 
/*    */   public Set getPool() {
/* 23 */     return new HashSet(1);
/*    */   }
/*    */ 
/*    */   public void send(Map item) {
/*    */   }
/*    */ 
/*    */   public Object getDestination(Map item) {
/* 30 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.nullRouter
 * JD-Core Version:    0.6.0
 */