/*    */ package net.sourceforge.owch2.router;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.WeakHashMap;
/*    */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*    */ import net.sourceforge.owch2.kernel.Agent;
/*    */ import net.sourceforge.owch2.kernel.Env;
/*    */ import net.sourceforge.owch2.kernel.Notification;
/*    */ 
/*    */ public class IPCRouter
/*    */   implements Router
/*    */ {
/*    */   private Map elements;
/*    */ 
/*    */   public IPCRouter()
/*    */   {
/* 12 */     elements = new WeakHashMap();
/*    */   }
/*    */   public void remove(Object key) {
/* 15 */     AbstractAgent n = (AbstractAgent)elements.get(key);
/* 16 */     n.handle_Dissolve(null);
/* 17 */     elements.remove(key);
/*    */   }
/*    */ 
/*    */   public void send(Map item) {
/* 21 */     Env.log(500, getClass().getName() + " sending item to" + getDestination(item));
/* 22 */     Agent node = (Agent)elements.get(getDestination(item));
/* 23 */     node.recv(new Notification(item));
/*    */   }
/*    */ 
/*    */   public Object getDestination(Map item) {
/* 27 */     return item.get("JMSDestination");
/*    */   }
/*    */ 
/*    */   public Set getPool() {
/* 31 */     return elements.keySet();
/*    */   }
/*    */ 
/*    */   public boolean hasElement(Object key) {
/* 35 */     return elements.containsKey(key);
/*    */   }
/*    */ 
/*    */   public void put(AbstractAgent node) {
/* 39 */     elements.put(node.getJMSReplyTo(), node);
/*    */   }
/*    */ 
/*    */   public boolean addElement(Map item) {
/* 43 */     if ((item instanceof Agent))
/*    */     {
/* 46 */       AbstractAgent n = (AbstractAgent)elements.get("JMSReplyTo");
/* 47 */       if (n != null) {
/* 48 */         n.handle_Dissolve(null);
/*    */       }
/* 50 */       elements.put(item.get("JMSReplyTo"), item);
/* 51 */       Env.log(500, getClass().getName() + " adding item " + item.get("JMSReplyTo"));
/* 52 */       return true;
/*    */     }
/* 54 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.IPCRouter
 * JD-Core Version:    0.6.0
 */