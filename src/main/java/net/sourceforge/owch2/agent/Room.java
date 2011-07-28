/*     */ package net.sourceforge.owch2.agent;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*     */ import net.sourceforge.owch2.kernel.Env;
/*     */ import net.sourceforge.owch2.kernel.LinkRegistry;
/*     */ import net.sourceforge.owch2.kernel.MetaAgent;
/*     */ import net.sourceforge.owch2.kernel.MetaProperties;
/*     */ import net.sourceforge.owch2.kernel.Notification;
/*     */ 
/*     */ public class Room extends AbstractAgent
/*     */   implements Runnable
/*     */ {
/*     */   public Map usersList;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  26 */     Map m = Env.parseCommandLineArgs(args);
/*  27 */     if (!m.containsKey("JMSReplyTo")) {
/*  28 */       Env.cmdLineHelp("\n\n******************** cmdline syntax error\nRoom Agent usage:\n\n-name name\n$Id: Room.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
/*     */     }
/*     */ 
/*  31 */     Room d = new Room(m);
/*  32 */     Thread t = new Thread();
/*     */     try {
/*  34 */       t.start();
/*     */       while (true)
/*  36 */         Thread.sleep(60000L);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public Room(Map m)
/*     */   {
/*  48 */     super(m);
/*  49 */     usersList = new LinkRegistry();
/*  50 */     Thread t = new Thread(this, "Room: " + getJMSReplyTo());
/*  51 */     t.start();
/*     */   }
/*     */ 
/*     */   synchronized void addUser(MetaProperties clientIn)
/*     */   {
/*  56 */     String userNode = clientIn.getJMSReplyTo();
/*  57 */     String userKey = (String)usersList.get(userNode);
/*  58 */     Env.log(8, "Room - addUser = " + userNode + "key = " + userKey);
/*  59 */     if (userKey == null)
/*  60 */       usersList.put(userNode, clientIn);
/*     */   }
/*     */ 
/*     */   public void handle_Test(MetaProperties nIn)
/*     */   {
/*  69 */     MetaProperties n = new Notification(Env.getLocation("http"));
/*  70 */     n.put("JMSDestination", nIn.get("JMSReplyTo"));
/*  71 */     n.put("JMSType", "Test");
/*  72 */     String url = n.get("URL") + "/test.jar";
/*  73 */     n.put("Path", url);
/*  74 */     n.put("Class", "owch.agent.TestWindow");
/*  75 */     n.put("args", "");
/*  76 */     send(n);
/*     */   }
/*     */ 
/*     */   public void publish(MetaProperties nIn)
/*     */   {
/*  83 */     String roomServerName = getJMSReplyTo();
/*  84 */     nIn.put("ResentFrom", roomServerName);
/*  85 */     nIn.remove("JMSMessageID");
/*     */ 
/*  87 */     for (Iterator e = usersList.keySet().iterator(); e.hasNext(); ) {
/*  88 */       String tmpJMSReplyTo = (String)e.next();
/*  89 */       nIn.put("JMSDestination", tmpJMSReplyTo);
/*     */ 
/*  91 */       Env.log(8, "Room.Publish (" + roomServerName + ")(" + nIn.getJMSReplyTo() + ") user is " + nIn.get("JMSDestination"));
/*     */ 
/*  93 */       send(new Notification(nIn));
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void userUpdate(MetaProperties clientIn)
/*     */   {
/* 103 */     addUser(clientIn);
/* 104 */     syncUsers(clientIn.get("JMSReplyTo").toString());
/*     */   }
/*     */ 
/*     */   public synchronized void syncUsers(String nodeNameIn) {
/* 108 */     String tmpJMSReplyTo = null;
/* 109 */     MetaProperties notificationOut = new Notification();
/* 110 */     Env.log(8, "Room - syncUsers  ");
/* 111 */     notificationOut.put("JMSType", "UserUpdate");
/* 112 */     notificationOut.put("SubJMSType", "Add");
/* 113 */     notificationOut.put("JMSDestination", nodeNameIn);
/* 114 */     String roomServerName = getJMSReplyTo();
/* 115 */     notificationOut.put("ResentFrom", roomServerName);
/*     */ 
/* 117 */     for (Iterator e = usersList.keySet().iterator(); e.hasNext(); ) {
/* 118 */       tmpJMSReplyTo = (String)e.next();
/* 119 */       notificationOut.put("JMSReplyTo", tmpJMSReplyTo);
/*     */ 
/* 121 */       Env.log(8, "Room.Publish (" + roomServerName + ")(" + notificationOut.getJMSReplyTo() + ") user is" + notificationOut.get("JMSDestination"));
/*     */ 
/* 123 */       send(new Notification(notificationOut));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 129 */     linkTo(Env.getParentNode().getJMSReplyTo());
/*     */ 
/* 131 */     while (!killFlag)
/*     */     {
/* 133 */       wait120();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final synchronized void wait120()
/*     */   {
/*     */     try
/*     */     {
/* 144 */       long tim = (long) (Math.random() * 180  * 1000 + 60000L);
/* 145 */       Env.log(12, "debug: wait120 Waiting for " + tim + " ms.");
/* 146 */       Thread.currentThread(); Thread.sleep(tim, 0);
/*     */     }
/*     */     catch (Exception e) {
/*     */     }
/* 150 */     Env.log(13, "debug: wait120 end");
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.Room
 * JD-Core Version:    0.6.0
 */