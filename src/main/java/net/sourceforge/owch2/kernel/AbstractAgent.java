/*     */ package net.sourceforge.owch2.kernel;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.TreeMap;
/*     */ import net.sourceforge.owch2.router.Router;
/*     */ 
/*     */ public abstract class AbstractAgent extends TreeMap
/*     */   implements Agent
/*     */ {
/*  17 */   protected static boolean killFlag = false;
/*     */   boolean virgin;
/*  19 */   LinkRegistry acl = null;
/*     */ 
/*  21 */   private static final Class[] cls_m = { MetaProperties.class };
/*     */ 
/*  23 */   private static final Class[] no_class = new Class[0];
/*  24 */   private static final Object[] no_Parm = new Object[0];
/*  25 */   private static final String default_val = "default".intern();
/*     */ 
/*     */   public Object getValue(String key)
/*     */   {
/*  32 */     Env.log(499, getClass().getName() + ":" + key);
/*  33 */     Object value = null;
/*  34 */     Class c = getClass();
/*     */     try {
/*  36 */       value = c.getField(key).get(this);
/*     */     }
/*     */     catch (Exception e) {
/*     */       try {
/*  40 */         Method m = c.getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1), no_class);
/*  41 */         value = m.invoke(this, no_Parm);
/*     */       }
/*     */       catch (Exception e1) {
/*  44 */         value = get(key);
/*     */       }
/*     */     }
/*  47 */     return value;
/*     */   }
/*     */ 
/*     */   public void putValue(String key, Object value)
/*     */   {
/*  57 */     String attempt = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
/*     */ 
/*  59 */     Env.log(499, getClass().getName() + ":" + key + "=" + value + "::" + value.getClass().getName());
/*     */ 
/*  61 */     Class c = getClass();
/*  62 */     Class[] vclass = { value.getClass() };
/*     */     try {
/*  64 */       Method m = c.getMethod(attempt, vclass);
/*  65 */       m.invoke(this, value);
/*     */     }
/*     */     catch (NoSuchMethodException nsm)
/*     */     {
/*  69 */       Env.log(499, attempt);
/*     */ 
/*  71 */       nsm.printStackTrace();
/*     */       try {
/*  73 */         Field f = c.getField(key);
/*  74 */         f.set(this, value);
/*     */       }
/*     */       catch (Exception e) {
/*  77 */         put(key, value);
/*     */ 
///*  79 */         jsr 29;
/*     */       }
/*     */     } catch (Exception e) {
/*  81 */       put(key, value);
/*     */     }
/*     */     finally
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isParent() {
/*  89 */     return false;
/*     */   }
/*     */ 
/*     */   public void linkTo(String lk)
/*     */   {
/*  97 */     if (lk == null) {
/*  98 */       Env.log(5, getClass().getName() + "::" + getJMSReplyTo() + ".link invoked. routing to default");
/*  99 */       lk = Env.getParentNode().getJMSReplyTo();
/*     */     }
/*     */ 
/* 102 */     MetaProperties n = new Notification();
/* 103 */     n.put("JMSDestination", lk);
/* 104 */     n.put("JMSType", "Link");
/* 105 */     send(n);
/*     */   }
/*     */ 
/*     */   public void send(MetaProperties n)
/*     */   {
/* 113 */     String d = null; String w = null;
/* 114 */     Agent node = null;
/*     */ 
/* 116 */     if (n.getJMSReplyTo() == null) {
/* 117 */       n.put("JMSReplyTo", getJMSReplyTo());
/*     */     }
/*     */ 
/* 120 */     d = (String)n.get("JMSDestination");
/* 121 */     if (d == null) {
/* 122 */       Env.log(8, "debug: AbstractAgent.Send(Notification) dropping unsendd Notification from " + getJMSReplyTo());
/* 123 */       return;
/*     */     }
/*     */ 
/* 126 */     Env.send(n);
/*     */   }
/*     */ 
/*     */   public final void recv(MetaProperties notificationIn) {
/* 130 */     String JMSType = (String)notificationIn.get("JMSType");
/*     */     try {
/* 132 */       getClass().getMethod("handle_" + JMSType, cls_m).invoke(this, notificationIn);
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 137 */       Env.log(2, "" + e.getTargetException() + " thrown within " + getJMSReplyTo() + "::" + getClass().getName() + "->" + JMSType);
/*     */ 
/* 139 */       e.getTargetException().printStackTrace();
/*     */     }
/*     */     catch (Exception e) {
/* 142 */       Env.log(2, "" + e + " thrown for " + getJMSReplyTo() + "::" + getClass().getName() + "->" + JMSType);
/*     */     }
/*     */   }
/*     */ 
/*     */   public AbstractAgent()
/*     */   {
/*     */   }
/*     */ 
/*     */   public AbstractAgent(Map proto) {
/* 151 */     super(proto);
/* 152 */     Env.getRouter("IPC").addElement(this);
/* 153 */     if (!isParent())
/* 154 */       linkTo("default");
/*     */   }
/*     */ 
/*     */   public void init(Map proto)
/*     */   {
/* 159 */     putAll(proto);
/* 160 */     Env.getRouter("IPC").addElement(this);
/* 161 */     if (!isParent())
/* 162 */       linkTo("default");
/*     */   }
/*     */ 
/*     */   public void handle_Dissolve(MetaProperties n)
/*     */   {
/* 168 */     killFlag = true;
/*     */ 
/* 170 */     Router[] r = { Env.getRouter("IPC"), Env.getRouter("owch"), Env.getRouter("http") };
/*     */ 
/* 175 */     for (int i = 0; i < r.length; i++)
/*     */       try {
/* 177 */         r[i].remove(this);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public void handle_Link(MetaProperties p)
/*     */   {
/* 187 */     String dest = p.getJMSReplyTo();
/* 188 */     MetaProperties n = new Notification();
/* 189 */     n.put("JMSType", "Update");
/* 190 */     n.put("JMSDestination", dest);
/* 191 */     send(n);
/* 192 */     Env.log(15, getClass().getName() + "::" + getJMSReplyTo() + " AbstractAgent.update() sent for " + dest);
/*     */   }
/*     */ 
/*     */   public void handle_Update(MetaProperties p)
/*     */   {
/* 201 */     String dest = p.getJMSReplyTo();
/* 202 */     MetaProperties n = new Notification();
/* 203 */     n.put("JMSType", "Updated");
/* 204 */     n.put("JMSDestination", dest);
/* 205 */     send(n);
/* 206 */     Env.log(15, getClass().getName() + "::" + getJMSReplyTo() + " AbstractAgent.update() sent for " + dest);
/*     */   }
/*     */ 
/*     */   public void handle_Unlink(MetaProperties m)
/*     */   {
/* 214 */     String lk = m.getJMSReplyTo();
/* 215 */     if (lk == null) {
/* 216 */       Env.log(5, getClass().getName() + "::" + getJMSReplyTo() + ".unlink invoked. routing to default");
/* 217 */       lk = Env.getParentNode().getJMSReplyTo();
/*     */     }
/*     */ 
/* 220 */     MetaProperties n = new Notification();
/* 221 */     n.put("JMSDestination", lk);
/* 222 */     n.put("JMSType", "UnLink");
/* 223 */     send(n);
/*     */   }
/*     */ 
/*     */   public final String getURL() {
/* 227 */     String s = (String)get("URL");
/* 228 */     return s;
/*     */   }
/*     */ 
/*     */   public final String getJMSReplyTo() {
/* 232 */     return (String)get("JMSReplyTo");
/*     */   }
/*     */ 
/*     */   public void handle_Move(MetaProperties notificationIn)
/*     */   {
/* 245 */     String host = notificationIn.get("Host").toString();
/* 246 */     if (host == null)
/* 247 */       host = Env.getHostname();
/*     */   }
/*     */ 
/*     */   public void clone_state1(String host)
/*     */   {
/* 253 */     MetaProperties n2 = Env.getLocation("http");
/* 254 */     n2.putAll(this);
/* 255 */     n2.put("JMSType", "DeployNode");
/* 256 */     n2.put("Class", getClass().getName());
/* 257 */     n2.put("JMSReplyTo", getJMSReplyTo());
/* 258 */     n2.put("Source", Env.getLocation("http").getURL() + get("Resource"));
/*     */ 
/* 261 */     n2.put("JMSDestination", host);
/* 262 */     send(n2);
/*     */   }
/*     */ 
/*     */   public void handle_Clone(MetaProperties n)
/*     */   {
/* 271 */     String host = n.get("Host").toString();
/* 272 */     if (host == null) {
/* 273 */       host = Env.getHostname();
/*     */     }
/* 275 */     clone_state1(host);
/*     */   }
/*     */ 
/*     */   public void relocate()
/*     */   {
/* 283 */     if (containsKey("Clone")) {
/* 284 */       String clist = (String)get("Clone");
/* 285 */       remove("Clone");
/* 286 */       Env.log(500, getClass().getName() + " **Cloning for " + clist);
/* 287 */       StringTokenizer st = new StringTokenizer(clist);
/* 288 */       while (st.hasMoreTokens()) {
/* 289 */         clone_state1(st.nextToken());
/*     */       }
/*     */     }
/*     */ 
/* 293 */     if (containsKey("Deploy"))
/*     */       try {
/* 295 */         String clist = (String)get("Deploy");
/* 296 */         remove("Deploy");
/* 297 */         Env.log(500, getClass().getName() + " **Cloning for " + clist);
/* 298 */         StringTokenizer st = new StringTokenizer(clist);
/* 299 */         while (st.hasMoreTokens()) {
/* 300 */           clone_state1(st.nextToken());
/*     */         }
/* 302 */         Thread.currentThread(); Thread.sleep(15000L);
/*     */ 
/* 304 */         System.exit(0);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 308 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.AbstractAgent
 * JD-Core Version:    0.6.0
 */