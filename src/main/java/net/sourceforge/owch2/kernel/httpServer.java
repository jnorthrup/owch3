/*     */ package net.sourceforge.owch2.kernel;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.URL;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ public class httpServer extends TCPServerWrapper
/*     */   implements ListenerReference, Runnable
/*     */ {
/*     */   int threads;
/*  15 */   private static final Map mimetypes = new HashMap();
/*     */ 
/*     */   public String getProtocol() {
/*  18 */     return "http";
/*     */   }
/*     */ 
/*     */   public long getExpiration() {
/*  22 */     return 0L;
/*     */   }
/*     */ 
/*     */   public int getThreads() {
/*  26 */     return threads;
/*     */   }
/*     */ 
/*     */   public ServerWrapper getServer() {
/*  30 */     return this;
/*     */   }
/*     */ 
/*     */   public void expire() {
/*  34 */     getServer().close();
/*     */   }
/*     */ 
/*     */   public httpServer(InetAddress hostAddr, int port, int threads) throws IOException {
/*  38 */     super(port, hostAddr);
/*  39 */     this.threads = threads;
/*     */     try {
/*  41 */       for (int i = 0; i < threads; i++)
/*  42 */         new Thread(this).start();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  46 */       Env.log(2, "httpServer creation Failure");
/*     */     }
/*     */   }
/*     */ 
/*     */   public MetaProperties getRequest(Socket s)
/*     */   {
/*  53 */     String line = "";
/*  54 */     Env.log(100, "httpServer.getRequest");
/*  55 */     Notification n = new Notification();
/*     */     try {
/*  57 */       n.setFormat("RFC822");
/*  58 */       DataInputStream ins = new DataInputStream(s.getInputStream());
/*     */ 
/*  60 */       line = ins.readLine();
/*  61 */       n.load(ins);
/*  62 */       n.put("Request", line);
/*     */     }
/*     */     catch (Exception e) {
/*  65 */       Env.log(5, "had a DynServer Snag, retry");
/*     */     }
/*  67 */     Env.log(50, "returning " + n.toString());
/*  68 */     return n;
/*     */   }
/*     */ 
/*     */   public void sendFile(Socket s, String file)
/*     */   {
/*     */     try
/*     */     {
/*  78 */       boolean found = true;
/*  79 */       byte[] pref = null;
/*  80 */       if (file.startsWith("/")) {
/*  81 */         file = file.substring(1);
/*     */       }
/*  83 */       FileInputStream is = null;
/*  84 */       File fd = null;
/*     */       try {
/*  86 */         fd = new File(file);
/*  87 */         is = new FileInputStream(file);
/*     */       }
/*     */       catch (Exception e) {
/*  90 */         found = false;
/*  91 */         pref = new String("HTTP/1.1 404 " + e.getMessage() + "\nConnection: close\n\n<!DOCTYPE HTML PUBLIC -//IETF//DTD HTML 2.0//EN><HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY><H1>" + e.getMessage() + "</H1>The requested URL " + file + " was not found on this server.<P></BODY></HTML>").getBytes();
/*     */       }
/*     */ 
/*  97 */       if (pref == null) {
/*  98 */         FileInputStream i = is;
/*  99 */         String p = "HTTP/1.1 200 OK\nContent-Type: " + getContentType(file) + "\n" + "Last-Modified: " + new SimpleDateFormat().format(new Date(fd.lastModified())) + "\n" + "Content-Length: " + fd.length() + "\n\n";
/*     */ 
/* 102 */         pref = p.getBytes();
/*     */       }
/*     */ 
/* 105 */       OutputStream os = new BufferedOutputStream(s.getOutputStream());
/* 106 */       os.write(pref, 0, pref.length);
/* 107 */       os.flush();
/* 108 */       if (found) {
/* 109 */         byte[] buf = new byte[Math.min(32768, (int)fd.length())];
/* 110 */         int actual = 0;
/* 111 */         int avail = 0;
/*     */         while (true) {
/* 113 */           avail = is.available();
/* 114 */           if (avail > 0) {
/* 115 */             actual = is.read(buf);
/*     */           }
/*     */           else {
/* 118 */             os.flush();
/* 119 */             break;
/*     */           }
/* 121 */           os.write(buf, 0, actual);
/* 122 */           Env.log(50, "httpd " + file + " sent " + actual + " bytes");
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 127 */       Env.log(20, "httpd " + file + " connection exception " + e.getMessage());
/*     */     }
/*     */     finally {
/*     */       try {
/* 131 */         Env.log(50, "httpd " + file + " connection closing");
/* 132 */         s.close();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void parseRequest(MetaProperties n) {
/* 141 */     String line = n.get("Request").toString();
/* 142 */     StringTokenizer st = new StringTokenizer(line);
/* 143 */     List list = new ArrayList();
/*     */ 
/* 145 */     while (st.hasMoreTokens()) {
/* 146 */       list.add(st.nextToken());
/*     */     }
/* 148 */     n.put("Method", list.get(0).toString().intern());
/* 149 */     n.put("Resource", list.get(1).toString());
/* 150 */     n.put("Protocol", list.get(2).toString());
/*     */   }
/*     */ 
/*     */   public void dispatchRequest(Socket s, MetaProperties n)
/*     */   {
/* 158 */     if (!Env.gethttpRegistry().dispatchRequest(s, n))
/* 159 */       sendFile(s, n.get("Resource").toString());
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 165 */     while (!Env.shutdown) {
/* 166 */       URL url = null;
/* 167 */       ArrayList list = new ArrayList();
/*     */       try
/*     */       {
/* 170 */         Env.log(20, "debug: " + Thread.currentThread().getName() + " init");
/* 171 */         Socket s = accept();
/* 172 */         MetaProperties n = getRequest(s);
/* 173 */         parseRequest(n);
/* 174 */         dispatchRequest(s, n);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 178 */         Env.log(2, "httpServer thread going down in flames on : " + e.getMessage());
/* 179 */         e.printStackTrace();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final String getContentType(String resource)
/*     */   {
/* 186 */     int li = resource.lastIndexOf(".");
/* 187 */     if (li == -1) {
/* 188 */       return "application/octet-stream";
/*     */     }
/* 190 */     resource = resource.substring(li + 1, resource.length());
/* 191 */     resource = resource.toLowerCase().trim();
/* 192 */     resource = (String)mimetypes.get(resource);
/* 193 */     if (resource == null) {
/* 194 */       return "application/octet-stream";
/*     */     }
/* 196 */     return resource;
/*     */   }
/*     */ 
/*     */   static {
/* 200 */     Env.log(50, "... loading up the mime types.");
/* 201 */     mimetypes.put("cpio", "application/x-cpio");
/* 202 */     mimetypes.put("ai", "application/postscript");
/* 203 */     mimetypes.put("eps", "application/postscript");
/* 204 */     mimetypes.put("ps", "application/postscript");
/* 205 */     mimetypes.put("aif", "audio/x-aiff");
/* 206 */     mimetypes.put("aiff", "audio/x-aiff");
/* 207 */     mimetypes.put("aifc", "audio/x-aiff");
/* 208 */     mimetypes.put("asc", "text/plain");
/* 209 */     mimetypes.put("txt", "text/plain");
/* 210 */     mimetypes.put("au", "audio/basic");
/* 211 */     mimetypes.put("snd", "audio/basic");
/* 212 */     mimetypes.put("avi", "video/x-msvideo");
/* 213 */     mimetypes.put("bcpio", "application/x-bcpio");
/* 214 */     mimetypes.put("bin", "application/octet-stream");
/* 215 */     mimetypes.put("dms", "application/octet-stream");
/* 216 */     mimetypes.put("lha", "application/octet-stream");
/* 217 */     mimetypes.put("lzh", "application/octet-stream");
/* 218 */     mimetypes.put("exe", "application/octet-stream");
/* 219 */     mimetypes.put("class", "application/octet-stream");
/* 220 */     mimetypes.put("bmp", "image/bmp");
/* 221 */     mimetypes.put("cpio", "application/x-cpio");
/* 222 */     mimetypes.put("cpt", "application/mac-compactpro");
/* 223 */     mimetypes.put("csh", "application/x-csh");
/* 224 */     mimetypes.put("css", "text/css");
/* 225 */     mimetypes.put("dcr", "application/x-director");
/* 226 */     mimetypes.put("dir", "application/x-director");
/* 227 */     mimetypes.put("dxr", "application/x-director");
/* 228 */     mimetypes.put("doc", "application/msword");
/* 229 */     mimetypes.put("dvi", "application/x-dvi");
/* 230 */     mimetypes.put("etx", "text/x-setext");
/* 231 */     mimetypes.put("ez", "application/andrew-inset");
/* 232 */     mimetypes.put("gif", "image/gif");
/* 233 */     mimetypes.put("gtar", "application/x-gtar");
/* 234 */     mimetypes.put("hdf", "application/x-hdf");
/* 235 */     mimetypes.put("hqx", "application/mac-binhex40");
/* 236 */     mimetypes.put("html", "text/html");
/* 237 */     mimetypes.put("htm", "text/html");
/* 238 */     mimetypes.put("ice", "x-conference/x-cooltalk");
/* 239 */     mimetypes.put("ief", "image/ief");
/* 240 */     mimetypes.put("igs", "model/iges");
/* 241 */     mimetypes.put("iges", "model/iges");
/* 242 */     mimetypes.put("jpeg", "image/jpeg");
/* 243 */     mimetypes.put("jpg", "image/jpeg");
/* 244 */     mimetypes.put("jpe", "image/jpeg");
/* 245 */     mimetypes.put("js", "application/x-javascript");
/* 246 */     mimetypes.put("latex", "application/x-latex");
/* 247 */     mimetypes.put("man", "application/x-troff-man");
/* 248 */     mimetypes.put("me", "application/x-troff-me");
/* 249 */     mimetypes.put("mid", "audio/midi");
/* 250 */     mimetypes.put("midi", "audio/midi");
/* 251 */     mimetypes.put("kar", "audio/midi");
/* 252 */     mimetypes.put("mif", "application/vnd.mif");
/* 253 */     mimetypes.put("movie", "video/x-sgi-movie");
/* 254 */     mimetypes.put("mpeg", "video/mpeg");
/* 255 */     mimetypes.put("mpg", "video/mpeg");
/* 256 */     mimetypes.put("mpe", "video/mpeg");
/* 257 */     mimetypes.put("mpga", "audio/mpeg");
/* 258 */     mimetypes.put("mp2", "audio/mpeg");
/* 259 */     mimetypes.put("mp3", "audio/mpeg");
/* 260 */     mimetypes.put("ms", "application/x-troff-ms");
/* 261 */     mimetypes.put("msh", "model/mesh");
/* 262 */     mimetypes.put("mesh", "model/mesh");
/* 263 */     mimetypes.put("silo", "model/mesh");
/* 264 */     mimetypes.put("nc", "application/x-netcdf");
/* 265 */     mimetypes.put("cdf", "application/x-netcdf");
/* 266 */     mimetypes.put("oda", "application/oda");
/* 267 */     mimetypes.put("pbm", "image/x-portable-bitmap");
/* 268 */     mimetypes.put("pdb", "chemical/x-pdb");
/* 269 */     mimetypes.put("xyz", "chemical/x-pdb");
/* 270 */     mimetypes.put("pdf", "application/pdf");
/* 271 */     mimetypes.put("pgm", "image/x-portable-graymap");
/* 272 */     mimetypes.put("pgn", "application/x-chess-pgn");
/* 273 */     mimetypes.put("png", "image/png");
/* 274 */     mimetypes.put("pnm", "image/x-portable-anymap");
/* 275 */     mimetypes.put("ppm", "image/x-portable-pixmap");
/* 276 */     mimetypes.put("ppt", "application/vnd.ms-powerpoint");
/* 277 */     mimetypes.put("qt", "video/quicktime");
/* 278 */     mimetypes.put("mov", "video/quicktime");
/* 279 */     mimetypes.put("ra", "audio/x-realaudio");
/* 280 */     mimetypes.put("ram", "audio/x-pn-realaudio");
/* 281 */     mimetypes.put("rm", "audio/x-pn-realaudio");
/* 282 */     mimetypes.put("ras", "image/x-cmu-raster");
/* 283 */     mimetypes.put("rgb", "image/x-rgb");
/* 284 */     mimetypes.put("rpm", "audio/x-pn-realaudio-plugin");
/* 285 */     mimetypes.put("rtf", "application/rtf");
/* 286 */     mimetypes.put("rtf", "text/rtf");
/* 287 */     mimetypes.put("rtx", "text/richtext");
/* 288 */     mimetypes.put("sgml", "text/sgml");
/* 289 */     mimetypes.put("sgm", "text/sgml");
/* 290 */     mimetypes.put("sh", "application/x-sh");
/* 291 */     mimetypes.put("shar", "application/x-shar");
/* 292 */     mimetypes.put("site.zip", "application/x-stuffit");
/* 293 */     mimetypes.put("skp", "application/x-koan");
/* 294 */     mimetypes.put("skd", "application/x-koan");
/* 295 */     mimetypes.put("skt", "application/x-koan");
/* 296 */     mimetypes.put("skm", "application/x-koan");
/* 297 */     mimetypes.put("smi", "application/smil");
/* 298 */     mimetypes.put("smil", "application/smil");
/* 299 */     mimetypes.put("spl", "application/x-futuresplash");
/* 300 */     mimetypes.put("src/", "application/x-wais-source");
/* 301 */     mimetypes.put("sv4cpio", "application/x-sv4cpio");
/* 302 */     mimetypes.put("sv4crc", "application/x-sv4crc");
/* 303 */     mimetypes.put("swf", "application/x-shockwave-flash");
/* 304 */     mimetypes.put("t", "application/x-troff");
/* 305 */     mimetypes.put("tr", "application/x-troff");
/* 306 */     mimetypes.put("roff", "application/x-troff");
/* 307 */     mimetypes.put("tar", "application/x-tar");
/* 308 */     mimetypes.put("tcl", "application/x-tcl");
/* 309 */     mimetypes.put("tex", "application/x-tex");
/* 310 */     mimetypes.put("texinfo", "application/x-texinfo");
/* 311 */     mimetypes.put("texi", "application/x-texinfo");
/* 312 */     mimetypes.put("tiff", "image/tiff");
/* 313 */     mimetypes.put("tif", "image/tiff");
/* 314 */     mimetypes.put("tsv", "text/tab-separated-values");
/* 315 */     mimetypes.put("ustar", "application/x-ustar");
/* 316 */     mimetypes.put("vcd", "application/x-cdlink");
/* 317 */     mimetypes.put("wav", "audio/x-wav");
/* 318 */     mimetypes.put("wrl", "model/vrml");
/* 319 */     mimetypes.put("vrml", "model/vrml");
/* 320 */     mimetypes.put("xbm", "image/x-xbitmap");
/* 321 */     mimetypes.put("xls", "application/vnd.ms-excel");
/* 322 */     mimetypes.put("xml", "text/xml");
/* 323 */     mimetypes.put("xpm", "image/x-xpixmap");
/* 324 */     mimetypes.put("xwd", "image/x-xwindowdump");
/* 325 */     mimetypes.put("zip", "application/zip");
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.httpServer
 * JD-Core Version:    0.6.0
 */