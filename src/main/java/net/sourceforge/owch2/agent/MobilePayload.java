/*     */ package net.sourceforge.owch2.agent;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Map;
import java.util.logging.Logger;
/*     */ import net.sourceforge.owch2.kernel.AbstractAgent;
/*     */ import net.sourceforge.owch2.kernel.Env;
/*     */ import net.sourceforge.owch2.kernel.Location;
/*     */ import net.sourceforge.owch2.kernel.MetaProperties;
/*     */ import net.sourceforge.owch2.kernel.Notification;
/*     */ import net.sourceforge.owch2.kernel.httpRegistry;
/*     */ import net.sourceforge.owch2.router.Router;
/*     */ 
/*     */ public class MobilePayload extends AbstractAgent
/*     */   implements Runnable
/*     */ {
/*     */   private Thread thread;
/*  58 */   protected Notification nice = new Notification();
/*     */ 
/*  61 */   protected final String[] nice_headers = { "Last-Modified", "Content-Type", "Content-Encoding" };
/*     */ 
/*  69 */   private long interval = 120000L;
/*     */   protected byte[] payload;
/*     */ 
/*     */   public MobilePayload(Map m)
/*     */   {
/*  25 */     super(m);
/*  26 */     if (containsKey("Source"))
/*  27 */       init((String)get("JMSReplyTo"), (String)get("Source"), (String)get("Resource"));
/*     */     else
/*  29 */       init((String)get("JMSReplyTo"), (String)get("Resource"));
/*     */   }
/*     */ 
/*     */   public MobilePayload(String name, String file)
/*     */   {
/*  35 */     init(name, file);
/*     */   }
/*     */ 
/*     */   public MobilePayload(String name, String url, String resource) {
/*  39 */     put("JMSReplyTo", name);
/*  40 */     put("Resource", resource);
/*  41 */     remove("Source");
/*  42 */     init(name, url, resource);
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/*  46 */     Map m = Env.parseCommandLineArgs(args);
/*  47 */     if ((!m.containsKey("JMSReplyTo")) || (!m.containsKey("Resource"))) {
/*  48 */       Env.cmdLineHelp("\n\n******************** cmdline syntax error\nMobilePayload Agent usage:\n\n-name name\n-Resource 'resource' -- the resource starting with '/' that is registered on the GateKeeper\n-Source 'file' -- the file \n[-Content-Type 'application/msword']\n[-Clone 'host1[ ..hostn]']\n[-Deploy 'host1[ ..hostn]']\n$Id: MobilePayload.java,v 1.1.1.1 2002/12/08 16:41:52 jim Exp $\n");
/*     */     }
/*     */ 
/*  54 */     new MobilePayload(m);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*  79 */     while (!killFlag) {
/*  80 */       sendRegistrations();
/*  81 */       relocate();
/*     */ 
/*  83 */       long tim = (long) (Math.random() * (interval / 2.0D) + interval / 2.0D);
/*  84 */       Env.log(12, getClass().getName() + " waiting for " + tim + " ms.");
/*     */       try {
/*  86 */         Thread.currentThread(); Thread.sleep(tim);
/*     */       } catch (InterruptedException e) {
/*  88 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handle_Dissolve(MetaProperties n)
/*     */   {
/*  97 */     thread.interrupt();
/*  98 */     Notification n2 = new Notification();
/*  99 */     n2.put("JMSDestination", "GateKeeper");
/* 100 */     n2.put("JMSType", "UnRegister");
/* 101 */     n2.put("URLSpec", get("Resource").toString());
/* 102 */     send(n2);
/* 103 */     super.handle_Dissolve(n);
/*     */   }
/*     */ 
/*     */   public void init(String name, String file)
/*     */   {
/* 125 */     put("JMSReplyTo", name);
/* 126 */     put("Resource", file);
/* 127 */     inductFile(file);
/*     */   }
/*     */ 
/*     */   public void init(String name, String url, String resource)
/*     */   {
/* 141 */     inductURL(url);
/*     */   }
/*     */ 
/*     */   public void inductURL(String url) {
/*     */     try {
/* 146 */       URL u = new URL(url);
/* 147 */       URLConnection uc = u.openConnection();
/* 148 */       for (int i = 0; i < nice_headers.length; i++) {
/* 149 */         String hdr = uc.getHeaderField(nice_headers[i]);
/* 150 */         if (hdr != null) {
/* 151 */           nice.put(nice_headers[i], get(nice_headers[i]));
/*     */         }
/*     */       }
/* 154 */       InputStream is = uc.getInputStream();
/* 155 */       inductStream(is);
/*     */     } catch (Exception e) {
/* 157 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void inductFile(String file) {
/*     */     try {
/* 163 */       String resource = file;
/* 164 */       if (resource.startsWith("/")) {
/* 165 */         resource = resource.substring(1);
/*     */       }
/*     */ 
/* 169 */       put("Resource", file);
/*     */ 
/* 171 */       InputStream is = new URL(file).openConnection().getInputStream();
/* 172 */       inductStream(is);
/*     */     } catch (Exception e) {
/* 174 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void inductStream(InputStream is) {
/* 179 */     Env.getRouter("IPC").addElement(this);
/*     */     try {
/* 181 */       ByteArrayOutputStream os = new ByteArrayOutputStream();
/* 182 */       byte[] buf = new byte[16384];
/* 183 */       int actual = 0;
/* 184 */       int avail = 0;
/* 185 */       while (actual != -1) {
/* 186 */         avail = is.available();
/* 187 */         actual = is.read(buf);
/* 188 */         if (actual >= 0) {
/* 189 */           os.write(buf, 0, actual);
/*     */ 
/* 191 */           Env.log(50, getClass().getName() + ":" + get("Resource").toString() + " slurped up " + actual + " bytes");
/*     */         }
/*     */       }
/*     */ 
/* 195 */       payload = os.toByteArray();
/* 196 */       os.flush();
/* 197 */       os.close();
/* 198 */       thread = new Thread(this, getClass().getName() + ":" + get("JMSReplyTo()") + ":" + get("Resource"));
/* 199 */       thread.start();
/* 200 */       Env.log(50, "-=-=-=-=-" + getClass().getName() + ":" + get("Resource").toString() + " ThreadStart ");
/*     */     } catch (Exception e) {
/* 202 */       Env.log(50, getClass().getName() + ":" + get("Resource").toString() + " failure " + e.getMessage());
/* 203 */       e.printStackTrace();
/* 204 */       return;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setInterval(long ival)
/*     */   {
/* 210 */     Thread.currentThread(); Thread.yield();
/* 211 */     interval = ival;
/*     */   }
/*     */ 
/*     */   public void sendRegistrations() {
/* 215 */     linkTo(null);
/* 216 */     String resource = get("Resource").toString();
/* 217 */     Location l = new Location(Env.getLocation("http"));
/* 218 */     l.put("JMSReplyTo", getJMSReplyTo());
/* 219 */     Env.gethttpRegistry().registerItem(resource, l);
/* 220 */     Notification n2 = new Notification();
/* 221 */     n2.put("JMSDestination", "GateKeeper");
/* 222 */     n2.put("JMSType", "Register");
/* 223 */     n2.put("URLSpec", resource);
/* 224 */     n2.put("URLFwd", l.getURL());
/* 225 */     send(n2);
/*     */   }
/*     */ 
/*     */   public void sendPayload(Socket s)
/*     */   {
/* 236 */     OutputStream os = null;
/*     */     try {
/* 238 */       if (!nice.containsKey("Content-Type")) {
/* 239 */         nice.put("Content-Type", "application/octet-stream");
/*     */       }
/* 241 */       if ((!nice.containsKey("Content-Length")) && ("text/html".intern() != nice.get("Content-Type"))) {
/* 242 */         nice.put("Content-Length", "" + payload.length);
/*     */       }
/* 244 */       os = s.getOutputStream();
/*     */ 
/* 246 */       os.write("HTTP/1.1 200 OK\n".getBytes());
/* 247 */       for (int i = 0; i < nice_headers.length; i++) {
/* 248 */         if (containsKey(nice_headers[i])) {
/* 249 */           nice.put(nice_headers[i], get(nice_headers[i]));
/*     */         }
/*     */       }
/*     */ 
/* 253 */       nice.save(os);
/*     */ 
/* 255 */       int actual = 0;
/* 256 */       int avail = 0;
/* 257 */       ByteArrayInputStream is = new ByteArrayInputStream(payload);
/* 258 */       byte[] buf = new byte[16384];
/*     */       do
/*     */       {
/* 261 */         while (is.available() > 0) {
/* 262 */           actual = is.read(buf);
/* 263 */           if (actual > 0) {
/* 264 */             Env.log(50, getClass().getName() + ":" + getJMSReplyTo() + " sent " + actual + " bytes");
/* 265 */             os.write(buf, 0, actual);
/*     */           }
/*     */         }
/*     */       }
/* 268 */       while (actual != -1);
/* 269 */       os.flush();
/* 270 */       os.close();
/* 271 */       s.close();
/*     */     } catch (Exception e) {
/* 273 */       Env.log(15, getClass().getName() + ":" + getJMSReplyTo() + " failure " + e.getMessage());
/* 274 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void handle_httpd(MetaProperties n) {
/* 279 */     Socket s = (Socket)n.get("_Socket");
/* 280 */     sendPayload(s);
/*     */   }
/*     */ 


    public void handle_WriteFile(MetaProperties n) {
        String path;
        path = (String) n.get("Path");
        String filename;
        filename = (String) n.get("Filename");


        File file = new File(path, filename);
        boolean overwrite;
        overwrite = false;
        if (overwrite = "true". equals(n.get("OverWrite"))
                &&
                file.exists()) {
            Logger.global.info("cannot overwrite file " + file.toString());
            return;
        }
        File tmpfile;
        tmpfile = new File(path, filename + "...");

        int blocksize;
        blocksize = 32 * 1024;
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(tmpfile), blocksize);
            out.write(payload);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        }
    }
 }