/*     */ package net.sourceforge.owch2.kernel;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.OutputStream;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.zip.Deflater;
/*     */ import java.util.zip.DeflaterOutputStream;
/*     */ import java.util.zip.InflaterInputStream;
/*     */ 
/*     */ public class PipeSocket
/*     */ {
/*  15 */   boolean isGet = false;
/*     */ 
/*  18 */   boolean isPut = false;
/*     */   Socket uc;
/*     */   Socket oc;
/*     */   InputStream oi;
/*     */   InputStream ci;
/*     */   OutputStream co;
/*     */   OutputStream oo;
/*     */   ThreadGroup tg;
/*  24 */   static int sc = 0;
/*  25 */   String label = "Generic Pipe" + sc++;
/*     */ 
/*  38 */   StreamDesc cEnc = new StreamDesc();
/*  39 */   StreamDesc oEnc = new StreamDesc();
/*     */ 
/*     */   public PipeSocket(Socket o)
/*     */   {
/*  34 */     oc = o;
/*     */   }
/*     */ 
/*     */   public PipeSocket(Socket o, StreamDesc in, StreamDesc out)
/*     */   {
/*  43 */     oc = o;
/*  44 */     cEnc = in;
/*  45 */     oEnc = out;
/*     */   }
/*     */ 
/*     */   public Object[] prepareStream(Socket sock, StreamDesc streamDesc)
/*     */     throws SocketException, IOException
/*     */   {
/*  51 */     sock.setSoTimeout(200);
/*  52 */     InputStream istream = sock.getInputStream();
/*  53 */     OutputStream ostream = sock.getOutputStream();
/*     */ 
/*  55 */     int Zsize = Math.max(128, streamDesc.getZbuf());
/*     */ 
/*  57 */     InputStream reader = streamDesc.usingInflate ? new InflaterInputStream(istream) : istream;
/*  58 */     OutputStream writer = streamDesc.usingDeflate ? new DeflaterOutputStream(ostream, new Deflater(1)) : ostream;
/*     */ 
/*  61 */     if (streamDesc.buffered) {
/*  62 */       int bb = streamDesc.bufbuf > 0 ? streamDesc.bufbuf : 32768;
/*  63 */       sock.setReceiveBufferSize(bb);
/*  64 */       sock.setSendBufferSize(bb);
/*  65 */       reader = new BufferedInputStream(reader, bb);
/*  66 */       writer = new BufferedOutputStream(writer, bb);
/*     */     }
/*  68 */     Object[] ret = { reader, writer };
/*  69 */     return ret;
/*     */   }
/*     */ 
/*     */   public void spin() {
/*  73 */     tg = new ThreadGroup("TG:" + label);
/*  74 */     new PipeThread(uc, oi, co, false, "PTInput:" + label, cEnc);
/*  75 */     new PipeThread(oc, ci, oo, false, "PTOutput:" + label, oEnc);
/*     */   }
/*     */ 
/*     */   public void connectTarget(Socket s)
/*     */     throws IOException
/*     */   {
/* 174 */     Object[] i = prepareStream(oc, oEnc);
/* 175 */     oi = ((InputStream)i[0]);
/* 176 */     oo = ((OutputStream)i[1]);
/* 177 */     i = prepareStream(this.uc = s, cEnc);
/* 178 */     ci = ((InputStream)i[0]);
/* 179 */     co = ((OutputStream)i[1]);
/*     */   }
/*     */ 
/*     */   public class PipeThread
/*     */     implements Runnable
/*     */   {
/*     */     InputStream is;
/*     */     OutputStream os;
/*     */     boolean term;
/*     */     int actual;
/*     */     int avail;
/*     */     Object pipe;
/*  91 */     final int blocksize = 18432;
/*  92 */     byte[] buf = new byte[18432];
/*     */     String name;
/*     */     StreamDesc sdesc;
/*     */ 
/*     */     public PipeThread(Object closeable, InputStream istream, OutputStream ostream, boolean terminate, String name, StreamDesc streamdesc)
/*     */     {
/*  97 */       pipe = closeable;
/*  98 */       is = istream;
/*  99 */       os = ostream;
/* 100 */       term = terminate;
/* 101 */       this.name = name;
/* 102 */       new Thread(tg, this, name).start();
/* 103 */       sdesc = streamdesc;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 108 */       if ((is instanceof InflaterInputStream))
/* 109 */         buf = new byte[40];
/* 110 */       while (!term)
/*     */         try {
/* 112 */           for (avail = is.available(); avail > 0; )
/*     */           {
/* 117 */             Env.log(500, label + " read has available bytes: " + avail);
/* 118 */             actual = is.read(buf);
/* 119 */             Env.log(500, label + " actual read: " + actual);
/* 120 */             if (actual == -1) {
/* 121 */               os.flush();
/* 122 */               term = true;
/* 123 */               Env.log(15, label + " input stream closed " + actual);
/* 124 */               if (term) {
/* 125 */                 os.close();
/*     */ 
/* 127 */                 pipe.getClass().getMethod("close", new Class[0]).invoke(pipe, new Object[0]);
/*     */ 
/* 134 */                 tg.interrupt();
/*     */               }
/*     */ 
/* 137 */               return;
/*     */             }
/* 139 */             Env.log(500, label + " output: " + actual);
/* 140 */             os.write(buf, 0, actual);
/*     */           }
/*     */ 
/* 145 */           Thread.currentThread(); Thread.sleep(100L);
/* 146 */           if ((os instanceof DeflaterOutputStream))
/*     */           {
/* 150 */             os.flush();
/*     */           }
/*     */         } catch (InterruptedIOException e) {
/*     */           try {
/* 154 */             os.flush();
/*     */           }
/*     */           catch (IOException e1) {
/*     */           }
/*     */         }
/*     */         catch (InterruptedException e) {
/* 160 */           Env.log(500, name + " closing: " + e.getMessage());
/* 161 */           return;
/*     */         }
/*     */         catch (Exception e) {
/* 164 */           Env.log(500, name + " Error - - closing: " + e.getMessage());
/* 165 */           return;
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.PipeSocket
 * JD-Core Version:    0.6.0
 */