/*     */ package net.sourceforge.owch2.agent;
/*     */ 
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*     */ import net.sourceforge.owch2.kernel.Env;
/*     */ import net.sourceforge.owch2.kernel.PipeFactory;
/*     */ import net.sourceforge.owch2.kernel.PipeSocket;
/*     */ import net.sourceforge.owch2.kernel.StreamDesc;
/*     */ 
/*     */ public class SocketProxy extends AbstractAgent
/*     */   implements Runnable
/*     */ {
/*  21 */   private Collection srcPort = new ArrayList(); private Collection srcHost = new ArrayList();
/*     */   private Iterator srcPort_i;
/*     */   private Iterator srcHost_i;
/*     */   String AgentPort;
/*     */   private ServerSocket ss;
/*  25 */   private PipeFactory pf = new PipeFactory();
/*     */ 
/*     */   public static void main(String[] args) {
/*  28 */     Map m = Env.parseCommandLineArgs(args);
/*  29 */     String[] ka = { "JMSReplyTo", "SourcePort", "SourceHost", "AgentPort" };
/*     */ 
/*  31 */     if (!m.keySet().containsAll(Arrays.asList(ka))) {
/*  32 */       Env.cmdLineHelp("\n\n******************** cmdline syntax error\nSocketProxy Agent usage:\n\n-name       (String)name\n-SourceHost (String)'hostname/IP[ ...n]'\n-SourcePort (int)'port[ ...n]'\n-AgentPort  (int)port\n[-Buffer (int)128+]\n[-{Inflate|Deflate} (String){agent|source|both} ..n]\n[-ZipBuf (int)<128+]]\n[-Clone 'host1[ ..hostn]']\n[-Deploy 'host1[ ..hostn]']\n$Id: SocketProxy.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
/*     */     }
/*     */ 
/*  45 */     SocketProxy d = new SocketProxy(m);
/*     */   }
/*     */ 
/*     */   public int getSourcePort() {
/*  49 */     if (!srcPort_i.hasNext())
/*  50 */       srcPort_i = srcPort.iterator();
/*  51 */     return ((Integer)srcPort_i.next()).intValue();
/*     */   }
/*     */ 
/*     */   public String getSourceHost() {
/*  55 */     if (!srcHost_i.hasNext())
/*  56 */       srcHost_i = srcHost.iterator();
/*  57 */     return (String)srcHost_i.next();
/*     */   }
/*     */ 
/*     */   public int getProxyPort() {
/*  61 */     return Integer.decode((String)get("AgentPort")).intValue();
/*     */   }
/*     */ 
/*     */   public SocketProxy(Map m)
/*     */   {
/*  69 */     super(m);
/*  70 */     StringTokenizer t = new StringTokenizer(m.get("SourcePort").toString());
/*     */ 
/*  72 */     while (t.hasMoreTokens()) {
/*  73 */       srcPort.add(Integer.decode(t.nextToken().toString()));
/*     */     }
/*     */ 
/*  77 */     t = new StringTokenizer(m.get("SourceHost").toString());
/*     */ 
/*  79 */     while (t.hasMoreTokens()) {
/*  80 */       srcHost.add(t.nextToken().toString());
/*     */     }
/*     */ 
/*  83 */     srcPort_i = srcPort.iterator();
/*  84 */     srcHost_i = srcHost.iterator();
/*     */     try
/*     */     {
/*  87 */       relocate();
/*  88 */       ss = new ServerSocket(getProxyPort());
/*  89 */       new Thread(this).start();
/*  90 */       spin();
/*     */     }
/*     */     catch (Exception e) {
/*  93 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void spin() {
/*  98 */     Thread t = new Thread();
/*     */     try {
/* 100 */       t.start();
/* 101 */       while (!killFlag)
/* 102 */         Thread.sleep(6000L);
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 106 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run() {
/* 111 */     while (!killFlag)
/*     */       try
/*     */       {
/* 114 */         Socket inbound = ss.accept();
/*     */ 
/* 116 */         PipeSocket ps = new PipeSocket(inbound, agentStreamDesc(), sourceStreamDesc());
/*     */ 
/* 119 */         ps.connectTarget(new Socket(getSourceHost(), getSourcePort()));
/* 120 */         ps.spin();
/*     */       }
/*     */       catch (InterruptedIOException e) {
/* 123 */         Env.log(500, getClass().getName() + "::interrupt " + e.getMessage());
/*     */       }
/*     */       catch (Exception e) {
/* 126 */         Env.log(10, getClass().getName() + "::run " + e.getMessage());
/* 127 */         e.printStackTrace();
/*     */       }
/*     */   }
/*     */ 
/*     */   private StreamDesc sourceStreamDesc()
/*     */   {
/* 133 */     String[] a = { "source", "both" };
/* 134 */     return new StreamDesc(containsKey("Inflate") ? Arrays.asList(a).contains(get("Inflate")) : false, containsKey("Deflate") ? Arrays.asList(a).contains(get("Deflate")) : false, containsKey("ZipBuf") ? Integer.decode(get("ZipBuf").toString()).intValue() : 4096, containsKey("Buffer") ? Integer.decode(get("Buffer").toString()).intValue() : 0);
/*     */   }
/*     */ 
/*     */   private StreamDesc agentStreamDesc()
/*     */   {
/* 142 */     String[] a = { "agent", "both" };
/* 143 */     return new StreamDesc(containsKey("Inflate") ? Arrays.asList(a).contains(get("Inflate")) : false, containsKey("Deflate") ? Arrays.asList(a).contains(get("Deflate")) : false, containsKey("ZipBuf") ? Integer.decode(get("ZipBuf").toString()).intValue() : 4096, containsKey("Buffer") ? Integer.decode(get("Buffer").toString()).intValue() : 0);
/*     */   }
/*     */ 
/*     */   public ServerSocket getSs()
/*     */   {
/* 151 */     return ss;
/*     */   }
/*     */ 
/*     */   public void setSs(ServerSocket ss) {
/* 155 */     this.ss = ss;
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.SocketProxy
 * JD-Core Version:    0.6.0
 */