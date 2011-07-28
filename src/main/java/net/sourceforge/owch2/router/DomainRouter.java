/*    */ package net.sourceforge.owch2.router;
/*    */ 
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.TreeMap;
/*    */ import net.sourceforge.owch2.kernel.Env;
/*    */ import net.sourceforge.owch2.kernel.Location;
/*    */ import net.sourceforge.owch2.kernel.MetaProperties;
/*    */ 
/*    */ public class DomainRouter
/*    */   implements Router
/*    */ {
/*    */   private Map elements;
/*    */ 
/*    */   public DomainRouter()
/*    */   {
/* 12 */     elements = new TreeMap();
/*    */   }
/*    */   public void remove(Object key) {
/* 15 */     elements.remove(key);
/*    */   }
/*    */ 
/*    */   public Object getDestination(Map item) {
/* 19 */     return item.get("Domain-Gateway");
/*    */   }
/*    */ 
/*    */   public Set getPool() {
/* 23 */     return null;
/*    */   }
/*    */ 
/*    */   public boolean addElement(Map item) {
/* 27 */     if (item.containsKey("Domain-Gateway")) {
/*    */       try {
/* 29 */         MetaProperties mp = new Location();
/* 30 */         mp.put("JMSReplyTo", item.get("JMSReplyTo").toString());
/*    */ 
/* 32 */         mp.put("Domain-Gateway", item.get("Domain-Gateway").toString());
/*    */ 
/* 34 */         elements.put(item.get("JMSReplyTo"), mp);
/* 35 */         return true;
/*    */       }
/*    */       catch (Exception e)
/*    */       {
/* 39 */         e.printStackTrace();
/*    */       }
/*    */     }
/* 42 */     return false;
/*    */   }
/*    */ 
/*    */   public void send(Map item) {
/* 46 */     Map domain = (Map)elements.get(item.get("JMSDestination"));
/*    */ 
/* 48 */     Object dest = domain.get("JMSReplyTo");
/* 49 */     item.put("JMSReplyTo", item.get("JMSReplyTo") + "@" + Env.getDomainName());
/*    */ 
/* 51 */     item.put("JMSDestination", dest);
/* 52 */     item.put("Domain-Gateway", Env.getDomainName());
/*    */   }
/*    */ 
/*    */   public boolean hasElement(Object key) {
/* 56 */     return elements.containsKey(key);
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.DomainRouter
 * JD-Core Version:    0.6.0
 */