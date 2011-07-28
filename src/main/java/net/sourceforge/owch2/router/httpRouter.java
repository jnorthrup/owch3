/*    */ package net.sourceforge.owch2.router;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ import java.net.Socket;
/*    */ import java.net.URL;
/*    */ import java.util.Date;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.TreeMap;
/*    */ import net.sourceforge.owch2.kernel.Env;
/*    */ import net.sourceforge.owch2.kernel.Location;
/*    */ import net.sourceforge.owch2.kernel.MetaAgent;
/*    */ import net.sourceforge.owch2.kernel.MetaProperties;
/*    */ import net.sourceforge.owch2.kernel.Notification;
/*    */ 
/*    */ public class httpRouter
/*    */   implements Router
/*    */ {
/* 15 */   static long ser = 0L;
/*    */   private Map elements;
/*    */   Socket p;
/*    */ 
/*    */   public httpRouter()
/*    */   {
/* 16 */     elements = new TreeMap();
/*    */   }
/*    */ 
/*    */   public void remove(Object key) {
/* 20 */     elements.remove(key);
/*    */   }
/*    */ 
/*    */   public Object getDestination(Map item) {
/* 24 */     return item.get("JMSDestination");
/*    */   }
/*    */ 
/*    */   public Set getPool() {
/* 28 */     return elements.keySet();
/*    */   }
/*    */ 
/*    */   public boolean hasElement(Object key) {
/* 32 */     return elements.containsKey(key);
/*    */   }
/*    */ 
/*    */   public boolean addElement(Map item) {
/* 36 */     Location met = new Location();
/* 37 */     met.put("JMSReplyTo", item.get("JMSReplyTo").toString());
/* 38 */     met.put("URL", item.get("URL").toString());
/* 39 */     elements.put(item.get("JMSReplyTo"), met);
/* 40 */     return true;
/*    */   }
/*    */ 
/*    */   public void send(Map item) {
/* 44 */     Notification n = new Notification(item);
/* 45 */     if (n.getJMSReplyTo() == null) {
/* 46 */       return;
/*    */     }
/* 48 */     Date d = new Date();
/* 49 */     String serr = n.get("JMSReplyTo") + ":" + n.get("JMSDestination").toString() + ":" + n.get("JMSType").toString() + "[" + d.toString() + "] " + ser++;
/*    */ 
/* 51 */     n.put("URL", Env.getLocation("http").getURL());
/* 52 */     MetaProperties prox = (MetaProperties)elements.get(n.get("JMSDestination"));
/* 53 */     if (prox == null) {
/* 54 */       prox = (MetaProperties)Env.getParentNode();
/*    */     }
/* 56 */     String u = prox.get("URL").toString();
/*    */     try
/*    */     {
/* 59 */       if (u == null) {
/* 60 */         if (Env.isParentHost()) {
/* 61 */           Env.log(2, "******Domain:  DROPPING PACKET FOR " + prox.get("JMSReplyTo"));
/* 62 */           return;
/*    */         }
/*    */ 
/* 65 */         u = Env.getParentNode().getURL();
/*    */       }
/*    */ 
/* 68 */       URL url = new URL(u);
/* 69 */       Socket s = new Socket(url.getHost(), url.getPort());
/* 70 */       OutputStream os = s.getOutputStream();
/* 71 */       os.write("POST /owch\n".getBytes());
/* 72 */       n.save(os);
/* 73 */       s.close();
/*    */     }
/*    */     catch (IOException e) {
/* 76 */       n.remove(u);
/* 77 */       Env.send(n);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.httpRouter
 * JD-Core Version:    0.6.0
 */