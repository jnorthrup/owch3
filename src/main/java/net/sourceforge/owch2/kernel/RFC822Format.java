/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.io.DataInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ public class RFC822Format
/*    */   implements Format
/*    */ {
/*    */   public void read(InputStream inputStream, Map map)
/*    */     throws IOException
/*    */   {
/* 17 */     DataInputStream ins = (inputStream instanceof DataInputStream) ? (DataInputStream)inputStream : new DataInputStream(inputStream);
/*    */     while (true) {
/* 19 */       String line = ins.readLine();
/* 20 */       if (line == null) {
/* 21 */         return;
/*    */       }
/* 23 */       int col = line.indexOf(':');
/* 24 */       if (col < 1) {
/* 25 */         return;
/*    */       }
/* 27 */       String key = line.substring(0, col).trim();
/* 28 */       String val = line.substring(col + 1).trim();
/* 29 */       map.put(key, val);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void write(OutputStream writer, Map map) throws IOException
/*    */   {
/* 35 */     Iterator iterator = map.keySet().iterator();
/* 36 */     while (iterator.hasNext()) {
/* 37 */       String key = (String)iterator.next();
/* 38 */       String line = key.toString() + ": " + map.get(key) + "\n";
/* 39 */       writer.write(line.getBytes());
/* 40 */       Env.log(200, "RFC822Format line saved:" + line);
/*    */     }
/* 42 */     writer.write(10);
/* 43 */     writer.flush();
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.RFC822Format
 * JD-Core Version:    0.6.0
 */