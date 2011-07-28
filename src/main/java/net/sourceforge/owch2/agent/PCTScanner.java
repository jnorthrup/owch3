/*     */ package net.sourceforge.owch2.agent;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Arrays;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*     */ import net.sourceforge.owch2.kernel.Env;
/*     */ import net.sourceforge.owch2.kernel.Notification;
/*     */ 
/*     */ public class PCTScanner extends AbstractAgent
/*     */ {
/*     */   protected static final int PORT = 4050;
/*     */   protected static final String CHANNEL_NAME = "store";
/*     */   protected static final String MSG_DEST = "JMSDestination";
/*     */ 
/*     */   public PCTScanner(Map m)
/*     */   {
/*  23 */     super(m);
/*     */     try
/*     */     {
/*  29 */       InetAddress theAddr = Env.getHostAddress();
/*     */       try
/*     */       {
/*  32 */         String host = theAddr.getHostAddress();
/*  33 */         Env.log(33, "attempting to bind addr: " + host);
/*     */ 
/*  35 */         theAddr = InetAddress.getByName(host);
/*  36 */         Env.log(33, "post-bind addr: " + theAddr);
/*     */       }
/*     */       catch (Exception e) {
/*     */       }
/*  40 */       int port = 4050;
/*     */       try {
/*  42 */         port = Integer.parseInt((String)get("AgentPort"));
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*  47 */       ServerSocket serverSocket = new ServerSocket(port, 16, theAddr);
/*  48 */       while (!killFlag) {
/*  49 */         Socket socket = serverSocket.accept();
/*  50 */         InputStream inputStream = socket.getInputStream();
/*  51 */         DataInputStream stream = new DataInputStream(inputStream);
/*  52 */         while (!socket.isInputShutdown())
/*     */         {
/*  54 */           String packet = stream.readLine();
/*     */ 
/*  56 */           int first = packet.indexOf('{');
/*     */ 
/*  58 */           int last = packet.lastIndexOf('}');
/*     */ 
/*  60 */           String line = packet.substring(first + 1, last);
/*     */ 
/*  62 */           char type = line.charAt(0);
/*     */ 
/*  64 */           char arg = line.charAt(1);
/*     */ 
/*  66 */           char flags = line.charAt(2);
/*     */ 
/*  68 */           String serialNo = line.substring(3, 17);
/*  69 */           String data = "";
/*     */           try
/*     */           {
/*  72 */             data = line.substring(18);
/*     */           }
/*     */           catch (StringIndexOutOfBoundsException e) {
/*     */           }
/*  76 */           new PCTMessage((command)command.getCodes().get(new Character(type)), arg, flags, serialNo, data);
/*  77 */           Notification notification = new Notification();
/*  78 */           Object value = get("JMSDestination");
/*  79 */           if (null != value)
/*  80 */             notification.put("JMSDestination", value);
/*  81 */           Map cmd_type = command.getCodes();
/*  82 */           command cmd = (command)cmd_type.get(new Character(type));
/*     */ 
/*  84 */           socket.getOutputStream().write(packet.getBytes());
/*     */ 
/*  87 */           notification.put("JMSType", cmd.getName());
/*  88 */           notification.put("PCTMessage.arg", new Character(arg));
/*  89 */           notification.put("PCTMessage.flags", new Character(flags));
/*  90 */           notification.put("PCTMessage.serial", serialNo);
/*  91 */           notification.put("PCTMessage.data", data);
/*  92 */           send(notification);
/*  93 */           notification.save(System.out);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (IOException e) {
/*  98 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) throws Exception {
/* 103 */     Map m = Env.parseCommandLineArgs(args);
/*     */ 
/* 105 */     String[] ka = { "JMSReplyTo", "JMSDestination", "AgentPort", "AgentHost" };
/*     */ 
/* 107 */     if (!m.keySet().containsAll(Arrays.asList(ka))) {
/* 108 */       Env.cmdLineHelp("\n\n******************** cmdline syntax error\nPCTScanner Agent usage:\n\n-name     (String)name\n-AgentPort   (int)port\n-JMSDestination  (String) The destination agent name\n$Id: PCTScanner.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
/*     */     }
/*     */ 
/* 117 */     PCTScanner d = new PCTScanner(m);
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.PCTScanner
 * JD-Core Version:    0.6.0
 */