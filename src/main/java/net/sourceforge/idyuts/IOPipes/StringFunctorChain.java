/*    */ package net.sourceforge.idyuts.IOPipes;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.StringFilter;
/*    */ 
/*    */ public class StringFunctorChain
/*    */   implements StringFunctor
/*    */ {
/*    */   StringFunctor[] filters;
/* 24 */   public static final Class[][] String_filters = { { String.class } };
/*    */ 
/* 32 */   private List _Stringclients = new ArrayList(1);
/*    */ 
/* 43 */   public static final Class[][] String_sources = { { String.class } };
/*    */ 
/*    */   public StringFunctorChain(StringFunctor[] f)
/*    */   {
/* 12 */     for (int i = 0; i < filters.length; i++)
/* 13 */       if (i > 0)
/* 14 */         filters[(i - 1)].attach(filters[i]);
/*    */   }
/*    */ 
/*    */   public void recv(String data)
/*    */   {
/* 20 */     filters[0].recv(data);
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 29 */     return String_filters;
/*    */   }
/*    */ 
/*    */   public void attach(StringFilter filter)
/*    */   {
/* 35 */     filters[(filters.length - 1)].attach(filter);
/*    */   }
/*    */ 
/*    */   public void detach(StringFilter filter) {
/* 39 */     filters[(filters.length - 1)].detach(filter);
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 48 */     return String_sources;
/*    */   }
/*    */ 
/*    */   public String fire(String s)
/*    */   {
/* 56 */     for (int ci = 0; ci < filters.length; ci++) {
/* 57 */       s = filters[ci].fire(s);
/*    */     }
/* 59 */     return s;
/*    */   }
/*    */ 
/*    */   public void xmit() {
/* 63 */     filters[0].xmit();
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.StringFunctorChain
 * JD-Core Version:    0.6.0
 */