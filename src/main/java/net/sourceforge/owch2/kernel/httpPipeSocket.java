/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.io.OutputStream;
/*    */ import java.net.Socket;
/*    */ 
/*    */ public class httpPipeSocket extends PipeSocket
/*    */ {
/*    */   public httpPipeSocket(Socket o, MetaAgent d, MetaProperties request)
/*    */   {
/* 19 */     super(o);
/*    */     try {
/* 21 */       Env.log(15, "httpPipeSocket:httpPipeSocket using MetaAgent : " + d.toString());
/* 22 */       URLString u = new URLString(d.getURL() + request.get("Resource").toString());
/* 23 */       Env.log(15, "httpPipeSocket:httpPipeSocket using URL: " + u);
/* 24 */       request.put("Host", u.getHost() + ":" + u.getPort());
/* 25 */       isGet = request.get("Method").toString().equals("GET");
/*    */ 
/* 27 */       uc= new Socket(u.getHost(), u.getPort());
/* 28 */       connectTarget(uc);
/* 29 */       request.setFormat("RFC822");
/* 30 */       co.write((request.get("Request") + "\n").getBytes());
/* 31 */       request.save(co);
/* 32 */       co.flush();
/* 33 */       spin();
/*    */     }
/*    */     catch (Exception e) {
/* 36 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.httpPipeSocket
 * JD-Core Version:    0.6.0
 */