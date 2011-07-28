/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.ArrayFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.ArraySource;
/*    */ import net.sourceforge.idyuts.IOLayer.intFilter;
/*    */ 
/*    */ public abstract class Assembler extends Sequencer
/*    */   implements ArraySource, intFilter
/*    */ {
/*    */   Object[] arr;
/* 38 */   private List _Array_clients = new ArrayList(1);
/*    */ 
/* 62 */   public static final Class[][] _Array_sources = { { new Object[0].getClass() } };
/*    */ 
/*    */   public Assembler()
/*    */   {
/* 15 */     arr = new Object[0];
/*    */   }
/*    */ 
/*    */   public Assembler(int len) {
/* 19 */     arr = new Object[len];
/*    */   }
/*    */ 
/*    */   public void recv(int data) {
/* 23 */     if (data >= arr.length) {
/* 24 */       xmit();
/*    */     }
/*    */     else
/*    */     {
/* 32 */       super.recv(data);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void attach(ArrayFilter filter)
/*    */   {
/* 41 */     _Array_clients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(ArrayFilter filter)
/*    */   {
/* 46 */     _Array_clients.remove(filter);
/*    */   }
/*    */ 
/*    */   public void xmit() {
/*    */     try {
/* 51 */       for (int ci = 0; ci < _Array_clients.size(); ci++) {
/* 52 */         ArrayFilter filter = (ArrayFilter)_Array_clients.get(ci);
/* 53 */         filter.recv(arr);
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 57 */       throw new Error("more debugging needed here");
/*    */     }
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 67 */     return _Array_sources;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.Assembler
 * JD-Core Version:    0.6.0
 */