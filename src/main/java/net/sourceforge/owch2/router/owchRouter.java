/*     */ package net.sourceforge.owch2.router;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import net.sourceforge.owch2.kernel.Env;
/*     */ import net.sourceforge.owch2.kernel.Location;
/*     */ import net.sourceforge.owch2.kernel.MetaAgent;
/*     */ import net.sourceforge.owch2.kernel.MetaProperties;
/*     */ import net.sourceforge.owch2.kernel.Notification;
/*     */ import net.sourceforge.owch2.kernel.URLString;
/*     */ import net.sourceforge.owch2.kernel.owchDispatch;
/*     */ 
/*     */ public class owchRouter
/*     */   implements Router
/*     */ {
/*  14 */   static long ser = 0L;
/*     */   private Map elements;
/*     */ 
/*     */   public owchRouter()
/*     */   {
/*  15 */     elements = new TreeMap();
/*     */   }
/*     */   public void remove(Object key) {
/*  18 */     elements.remove(key);
/*     */   }
/*     */ 
/*     */   public Object getDestination(Map item) {
/*  22 */     return item.get("JMSDestination");
/*     */   }
/*     */ 
/*     */   public Set getPool() {
/*  26 */     return elements.keySet();
/*     */   }
/*     */ 
/*     */   public boolean hasElement(Object key) {
/*  30 */     return elements.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean addElement(Map item)
/*     */   {
/*  35 */     Location location = new Location();
/*  36 */     decorateProxy(location, item);
/*  37 */     return true;
/*     */   }
/*     */ 
/*     */   private void decorateProxy(Location location, Map item) {
/*  41 */     location.put("JMSReplyTo", item.get("JMSReplyTo").toString());
/*     */     try {
/*  43 */       location.put("URL", item.get("URL").toString());
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/*  47 */     elements.put(item.get("JMSReplyTo"), location);
/*     */   }
/*     */ 
/*     */   public void send(Map item) {
/*  51 */     Notification n = new Notification(item);
/*  52 */     if (n.getJMSReplyTo() == null) {
/*  53 */       return;
/*     */     }
/*  55 */     String serial = serr(n);
/*  56 */     MetaProperties outProx = PrepareDelivery(n, serial);
/*     */     String u;
/*  59 */     if (!outProx.containsKey("URL")) {
/*  60 */       if (Env.isParentHost()) {
/*  61 */         Env.log(2, "******Domain:  DROPPING PACKET FOR " + outProx.get("JMSReplyTo"));
/*  62 */         return;
/*     */       }
/*     */ 
/*  65 */       u = Env.getParentNode().getURL();
/*     */     }
/*     */     else
/*     */     {
/*  69 */       u = outProx.get("URL").toString();
/*     */     }
/*     */ 
/*  73 */     URLString url = new URLString(u);
/*     */     try {
/*  75 */       byte[] buf = createByteBuffer(n);
/*  76 */       DatagramPacket p = new DatagramPacket(buf, buf.length, dest(h(url)), url.getPort());
/*  77 */       Env.getowchDispatch().handleDatagram(serial, p, n.get("Priority") != null);
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private MetaProperties PrepareDelivery(Notification n, String serial)
/*     */   {
/*  86 */     n.put("JMSMessageID", serial);
/*  87 */     n.put("URL", Env.getLocation("owch").getURL());
/*  88 */     MetaProperties outProx = getProxy(n);
/*  89 */     return outProx;
/*     */   }
/*     */ 
/*     */   public byte[] createByteBuffer(Notification n) throws IOException {
/*  93 */     ByteArrayOutputStream os = new ByteArrayOutputStream();
/*  94 */     n.save(os);
/*  95 */     byte[] buf = os.toString().getBytes();
/*  96 */     return buf;
/*     */   }
/*     */ 
/*     */   private MetaProperties getProxy(Notification n) {
/* 100 */     MetaProperties prox = (MetaProperties)elements.get(n.get("JMSDestination"));
/* 101 */     if (prox == null) {
/* 102 */       prox = (MetaProperties)Env.getParentNode();
/*     */     }
/* 104 */     return prox;
/*     */   }
/*     */ 
/*     */   private String serr(Notification n) {
/* 108 */     return n.get("JMSReplyTo") + ":" + n.get("JMSDestination").toString() + ":" + n.get("JMSType").toString() + "[" + d().toString() + "] " + ser++;
/*     */   }
/*     */ 
/*     */   private Date d()
/*     */   {
/* 113 */     return new Date();
/*     */   }
/*     */ 
/*     */   private String h(URLString url) {
/* 117 */     return url.getHost();
/*     */   }
/*     */ 
/*     */   private InetAddress dest(String h) throws UnknownHostException {
/* 121 */     return InetAddress.getByName(h);
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.owchRouter
 * JD-Core Version:    0.6.0
 */