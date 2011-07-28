/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ public class StreamDesc
/*    */ {
/* 13 */   protected boolean usingInflate = false; protected boolean usingDeflate = false;
/* 14 */   protected boolean buffered = false;
/* 15 */   protected int zbuf = 0; protected int bufbuf = 0;
/*    */ 
/*    */   public StreamDesc() {
/*    */   }
/* 19 */   public StreamDesc(int bb) { buffered = (bb > 128); }
/*    */ 
/*    */   public StreamDesc(boolean zencrypt, boolean zdecrypt, int zbuff, int bb)
/*    */   {
/* 23 */     usingInflate = ((zencrypt) && (zbuff > 128));
/* 24 */     usingDeflate = ((zdecrypt) && (zbuff > 128));
/* 25 */     buffered = (bb > 128);
/* 26 */     zbuf = zbuff;
/* 27 */     bufbuf = bb;
/*    */   }
/*    */   public boolean isBuffered() {
/* 30 */     return buffered;
/*    */   }
/*    */   public int getZbuf() {
/* 33 */     return zbuf;
/*    */   }
/*    */   public int getBufbuf() {
/* 36 */     return bufbuf;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.StreamDesc
 * JD-Core Version:    0.6.0
 */