/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.intFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.intSource;
/*    */ 
/*    */ public class RangeCounter
/*    */   implements intSource
/*    */ {
/*    */   int start;
/*    */   int finish;
/* 20 */   private List _Int_clients = new ArrayList(1);
/*    */ 
/* 31 */   public static final Class[][] Int_sources = { { Integer.TYPE } };
/*    */ 
/*    */   public RangeCounter(int finish)
/*    */   {
/* 12 */     this(0, finish);
/*    */   }
/*    */ 
/*    */   public RangeCounter(int start, int finish) {
/* 16 */     this.finish = finish;
/* 17 */     this.start = start;
/*    */   }
/*    */ 
/*    */   public void attach(intFilter filter)
/*    */   {
/* 23 */     _Int_clients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(intFilter filter) {
/* 27 */     _Int_clients.remove(filter);
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 36 */     return Int_sources;
/*    */   }
/*    */ 
/*    */   public synchronized void xmit() {
/*    */     try {
/* 41 */       for (int x = start; x <= finish; x++) {
/* 42 */         int ii = x;
/* 43 */         for (int ci = 0; ci < _Int_clients.size(); ci++) {
/* 44 */           intFilter filter = (intFilter)_Int_clients.get(ci);
/* 45 */           filter.recv(ii);
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 50 */       throw new Error("more debugging needed here");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.RangeCounter
 * JD-Core Version:    0.6.0
 */