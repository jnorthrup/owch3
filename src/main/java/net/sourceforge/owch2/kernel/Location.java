/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.util.Map;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class Location extends TreeMap
/*    */   implements MetaProperties
/*    */ {
/* 29 */   private String format = "RFC822";
/*    */ 
/*    */   public static Location create(ListenerReference lr)
/*    */   {
/* 36 */     String tstring = lr.getProtocol() + ":";
/* 37 */     Location l = new Location();
/* 38 */     tstring = tstring + "//" + Env.getHostname().trim() + ":" + (Env.getHostPort() == 0 ? lr.getServer().getLocalPort() : Env.getHostPort());
/*    */ 
/* 40 */     l.put("URL", tstring);
/* 41 */     return l;
/*    */   }
/*    */ 
/*    */   public final void load(InputStream reader)
/*    */     throws IOException
/*    */   {
/* 50 */     Env.getFormat(getFormat()).read(reader, this);
/*    */   }
/*    */ 
/*    */   public synchronized void save(OutputStream writer) throws IOException
/*    */   {
/* 55 */     Env.getFormat(getFormat()).write(writer, this);
/*    */   }
/*    */ 
/*    */   public void setFormat(String format) {
/* 59 */     this.format = format;
/*    */   }
/*    */ 
/*    */   public String getFormat() {
/* 63 */     return format;
/*    */   }
/*    */ 
/*    */   public final String getURL() {
/* 67 */     String s = (String)get("URL");
/* 68 */     return s;
/*    */   }
/*    */ 
/*    */   public final String getJMSReplyTo()
/*    */   {
/* 73 */     return (String)get("JMSReplyTo");
/*    */   }
/*    */ 
/*    */   public Location()
/*    */   {
/*    */   }
/*    */ 
/*    */   public Location(Map p)
/*    */   {
/* 85 */     putAll(p);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/*    */     try
/*    */     {
/* 15 */       Env.registerFormat("XMLSerial", (Format)Class.forName("msg.format.XMLSerialFormat").newInstance());
/*    */     }
/*    */     catch (Exception e) {
/* 18 */       Env.log(5, "XML Format not loaded");
/*    */     }
/* 20 */     Env.registerFormat("RFC822", new RFC822Format());
/*    */     try {
/* 22 */       Env.registerFormat("Serial", (Format)Class.forName("msg.format.SerialFormat").newInstance());
/*    */     }
/*    */     catch (Exception e) {
/* 25 */       Env.log(5, "Serial Format not loaded");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.Location
 * JD-Core Version:    0.6.0
 */