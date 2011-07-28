/*    */ package net.sourceforge.idyuts.IOConversion;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import net.sourceforge.idyuts.IOLayer.StringFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.StringSource;
/*    */ import net.sourceforge.idyuts.IOLayer.intFilter;
/*    */ 
/*    */ public class IntStringConverter
/*    */   implements intFilter, StringSource
/*    */ {
/*    */   protected String data;
/* 20 */   public static final Class[][] _Int_filters = { { Integer.TYPE } };
/*    */   private List _String_clients;
/* 56 */   public static final Class[][] _String_sources = { { String.class } };
/*    */ 
/*    */   public IntStringConverter()
/*    */   {
/* 31 */     _String_clients = new ArrayList(1);
/*    */   }
/*    */ 
/*    */   public void recv(int evt)
/*    */   {
/* 15 */     data = ("" + evt + "L");
/* 16 */     xmit();
/*    */   }
/*    */ 
/*    */   public Class[][] getFilters()
/*    */   {
/* 26 */     return _Int_filters;
/*    */   }
/*    */ 
/*    */   public void attach(StringFilter filter)
/*    */   {
/* 34 */     _String_clients.add(filter);
/*    */   }
/*    */ 
/*    */   public void detach(StringFilter filter) {
/* 38 */     _String_clients.remove(filter);
/*    */   }
/*    */ 
/*    */   public void xmit()
/*    */   {
/*    */     try {
/* 44 */       for (int ci = 0; ci < _String_clients.size(); ci++) {
/* 45 */         StringFilter filter = (StringFilter)_String_clients.get(ci);
/* 46 */         filter.recv(data);
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 50 */       e.printStackTrace();
/* 51 */       throw new Error("more debugging needed here");
/*    */     }
/*    */   }
/*    */ 
/*    */   public Class[][] getSources()
/*    */   {
/* 63 */     return _String_sources;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOConversion.IntStringConverter
 * JD-Core Version:    0.6.0
 */