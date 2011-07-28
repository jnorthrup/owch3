/*     */ package net.sourceforge.owch2.agent;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Map;
/*     */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*     */ import net.sourceforge.owch2.kernel.Env;
/*     */ import net.sourceforge.owch2.kernel.PipeFactory;
/*     */ import net.sourceforge.owch2.kernel.PipeSocket;
/*     */ 
/*     */ public class SocksProxy extends AbstractAgent
/*     */   implements Runnable
/*     */ {
/*     */   private ServerSocket ss;
/*  18 */   static final String[] errs = { "succeeded", "general SOCKS server failure", "connection not allowed by ruleset", "Network unreachable", "Host unreachable", "Connection refused", "TTL expired", "Command not supported", "Address type not supported", "to X'FF' unassigned" };
/*     */ 
/*  79 */   PipeFactory pf = new PipeFactory();
/*  80 */   private int socksPort = 1080;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  25 */     Map m = Env.parseCommandLineArgs(args);
/*  26 */     if ((!m.containsKey("JMSReplyTo")) || (!m.containsKey("SocksHost")) || (!m.containsKey("SourcePort")) || (!m.containsKey("SourceHost")) || (!m.containsKey("AgentPort")))
/*     */     {
/*  28 */       Env.cmdLineHelp("\n\n******************** cmdline syntax error\nSocketProxy Agent usage:\n\n-name       (String)name\n-SourceHost (String)hostname/IP\n-SocksHost (String)hostname/IP\n-SourcePort (int)port\n-AgentPort  (int)port\n[-SocksPort (int)port]\n[-Clone 'host1[ ..hostn]']\n[-Deploy 'host1[ ..hostn]']\n$Id: SocksProxy.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
/*     */     }
/*     */ 
/*  36 */     SocketProxy d = new SocketProxy(m);
/*  37 */     Thread t = new Thread();
/*     */     try {
/*  39 */       t.start();
/*  40 */       while (!Env.shutdown)
/*  41 */         Thread.sleep(6000L);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  45 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getSourcePort() {
/*  50 */     return Integer.decode((String)get("SourcePort")).intValue();
/*     */   }
/*     */ 
/*     */   public int getSocksPort() {
/*  54 */     if (containsKey("SocksPort")) {
/*  55 */       return Integer.decode((String)get("SocksPort")).intValue();
/*     */     }
/*     */ 
/*  58 */     return 1080;
/*     */   }
/*     */ 
/*     */   public int getProxyPort()
/*     */   {
/*  63 */     return Integer.decode((String)get("AgentPort")).intValue();
/*     */   }
/*     */ 
/*     */   public SocksProxy(Map m)
/*     */   {
/*  68 */     super(m);
/*     */     try {
/*  70 */       relocate();
/*  71 */       setSs(new ServerSocket(getProxyPort()));
/*     */     }
/*     */     catch (Exception e) {
/*  74 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  95 */     while (!killFlag)
/*     */       try
/*     */       {
/*  98 */         Socket inbound = getSs().accept();
/*     */ 
/* 100 */         PipeSocket ps = new PipeSocket(inbound);
/* 101 */         Socket socks = new Socket((String)get("SocksHost"), getSocksPort());
/* 102 */         byte[] app = { 0 };
/*     */ 
/* 105 */         byte ver = 5; byte napp = (byte)app.length;
/* 106 */         socks.getOutputStream().write(new byte[] { ver, napp });
/*     */ 
/* 108 */         socks.getOutputStream().write(app);
/* 109 */         byte[] resp = new byte[2];
/* 110 */         socks.getInputStream().read();
/* 111 */         if ((resp[0] != 5) || (resp[1] != 0))
/*     */         {
/* 113 */           Env.log(2, getClass().getName() + " Socks proxy failures returned other than socks5 Auth0; aborting  ");
/*     */ 
/* 115 */           inbound.close();
/* 116 */           return;
/*     */         }
/*     */ 
/* 119 */         send_request(socks);
/* 120 */         if (handle_response(socks)) {
/* 121 */           ps.connectTarget(socks);
/* 122 */           ps.spin();
/*     */         }
/*     */       }
/*     */       catch (InterruptedIOException e) {
/* 126 */         Env.log(500, getClass().getName() + "::interrupt " + e.getMessage());
/*     */       }
/*     */       catch (Exception e) {
/* 129 */         Env.log(10, getClass().getName() + "::run " + e.getMessage());
/* 130 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   void send_request(Socket socks)
/*     */   {
/*     */     try
/*     */     {
/* 168 */       DataOutputStream os = new DataOutputStream(socks.getOutputStream());
/* 169 */       short sport = (short)getSourcePort();
/* 170 */       byte VER = 5; byte CMD = 1;
/* 171 */       byte RSV = 0; byte ATYP = 3; byte[] DST_ADDR = get("SourceHost").toString().getBytes();
/* 172 */       byte DST_ADDR_LEN = (byte)DST_ADDR.length;
/* 173 */       os.write(VER);
/* 174 */       os.write(CMD);
/* 175 */       os.write(RSV);
/* 176 */       os.write(ATYP);
/* 177 */       os.write(DST_ADDR_LEN);
/* 178 */       os.write(DST_ADDR);
/* 179 */       os.write(sport);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 183 */       Env.log(2, getClass().getName() + "::handle_socks_reply threw " + e.getClass().getName() + "/" + e.getMessage());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean handle_response(Socket socks)
/*     */   {
/*     */     try
/*     */     {
/* 231 */       DataInputStream is = new DataInputStream(socks.getInputStream());
/*     */ 
/* 233 */       byte VER = (byte)is.read(); byte REP = (byte)is.read(); byte RSV = (byte)is.read(); byte ATYP = (byte)is.read();
/* 234 */       byte BND_ADDR_LEN = (byte)is.read();
/* 235 */       byte[] BND_ADDR = new byte[BND_ADDR_LEN];
/* 236 */       is.read(BND_ADDR);
/* 237 */       short BND_PORT = is.readShort();
/*     */ 
/* 239 */       Env.log(15, getClass().getName() + "::Connect request returned " + " VER:" + VER + " REP:" + errs[REP] + " ATYP:" + ATYP + " " + new String(BND_ADDR) + " BND_PORT:" + BND_PORT);
/*     */ 
/* 241 */       return REP == 0;
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 245 */       Env.log(2, getClass().getName() + "::handle_socks_reply threw " + e.getClass().getName() + "/" + e.getMessage());
/*     */     }
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   ServerSocket getSs()
/*     */   {
/* 252 */     return ss;
/*     */   }
/*     */ 
/*     */   void setSs(ServerSocket ss) {
/* 256 */     this.ss = ss;
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.SocksProxy
 * JD-Core Version:    0.6.0
 */