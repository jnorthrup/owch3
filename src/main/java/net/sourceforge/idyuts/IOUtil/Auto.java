/*    */ package net.sourceforge.idyuts.IOUtil;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.HashSet;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ public final class Auto
/*    */ {
/*    */   private static final boolean dbg_output = false;
/* 34 */   private static final Class[] examplar = new Class[0];
/*    */ 
/*    */   private static void walkParents(Class c, Collection col)
/*    */   {
/* 17 */     Collection clt = new ArrayList();
/* 18 */     clt.add(c.getSuperclass());
/* 19 */     clt.addAll(Arrays.asList(c.getInterfaces()));
/* 20 */     for (Iterator i = clt.iterator(); i.hasNext(); ) {
/* 21 */       Class t = (Class)i.next();
/* 22 */       if (t == null) {
/*    */         continue;
/*    */       }
/* 25 */       col.add(t);
/*    */ 
/* 27 */       walkParents(t, col);
/*    */     }
/*    */ 
/* 30 */     col.addAll(clt);
/*    */   }
/*    */ 
/*    */   public static boolean attach(Object source, Object filter)
/*    */   {
/*    */     try
/*    */     {
/* 38 */       List l = new ArrayList();
/*    */ 
/* 41 */       HashSet hs = new HashSet();
/* 42 */       walkParents(filter.getClass(), hs);
/* 43 */       Class[] ifs = (Class[])hs.toArray(examplar);
/*    */ 
/* 47 */       Method[] app = source.getClass().getMethods();
/* 48 */       Method the1 = null;
/* 49 */       for (int i = 0; i < app.length; i++) {
/* 50 */         if ("attach".intern() == app[i].getName().intern()) {
/* 51 */           Class[] cl = app[i].getParameterTypes();
/* 52 */           for (int j = 0; j < ifs.length; j++)
/*    */           {
/* 55 */             if (cl[0] != ifs[j])
/*    */             {
/*    */               continue;
/*    */             }
/* 59 */             the1 = app[i];
/* 60 */             break;
/*    */           }
/*    */ 
/*    */         }
/*    */ 
/* 67 */         if (the1 != null) {
/* 68 */           the1.invoke(source, new Object[] { filter });
/*    */ 
/* 70 */           return true;
/*    */         }
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 75 */       e.printStackTrace();
/* 76 */       return false;
/*    */     }
/* 78 */     return false;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOUtil.Auto
 * JD-Core Version:    0.6.0
 */