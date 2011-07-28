/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.intFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.intSource;
/*    */ 
/*    */ public class Three
/*    */   implements intSource
/*    */ {
/*    */   private List _int_clients;
/*    */   public static final int data = 3;
/* 21 */   private static Class[] foo = { Integer.TYPE };
/*    */ 
/* 37 */   public static final Class[][] _int_sources = { { Integer.TYPE } };
/*    */ 
/*    */   public Three()
/*    */   {
/*  9 */     _int_clients = new ArrayList(1);
/*    */   }
/*    */   public void attach(intFilter filter) {
/* 12 */     _int_clients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(intFilter filter) {
/* 16 */     _int_clients.remove(filter);
/*    */   }
/*    */ 
/*    */   public void xmit()
/*    */   {
/*    */     try
/*    */     {
/* 26 */       for (int ci = 0; ci < _int_clients.size(); ci++) {
/* 27 */         intFilter filter = (intFilter)_int_clients.get(ci);
/* 28 */         filter.recv(3);
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 32 */       throw new Error("more debugging needed here");
/*    */     }
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 44 */     return _int_sources;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.Three
 * JD-Core Version:    0.6.0
 */