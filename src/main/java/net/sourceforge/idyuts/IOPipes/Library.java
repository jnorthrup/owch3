/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import net.sourceforge.idyuts.IOLayer.Filter;
/*    */ import net.sourceforge.idyuts.IOLayer.Source;
/*    */ 
/*    */ public abstract class Library extends Sequencer
/*    */   implements Source
/*    */ {
/*    */   Filter[] library;
/*    */   Object data;
/* 14 */   private static Class[] foo = { Object.class };
/*    */ 
/*    */   Library(Filter[] l)
/*    */   {
/*  8 */     library = l;
/*    */   }
/*    */ 
/*    */   public synchronized void xmit()
/*    */   {
/*    */     try
/*    */     {
/* 18 */       Filter filter = library[(index % library.length)];
/* 19 */       if (filter == null) {
/* 20 */         return;
/*    */       }
/* 22 */       filter.getClass().getMethod("recv", foo).invoke(filter, new Object[] { data });
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 26 */       throw new Error("need more debugging here");
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.Library
 * JD-Core Version:    0.6.0
 */