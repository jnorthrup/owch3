/*    */ package net.sourceforge.idyuts.IOPipes.functor;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ import net.sourceforge.idyuts.IOPipes.StringFunctorImpl;
/*    */ 
/*    */ public class BorrowedFunctor extends StringFunctorImpl
/*    */ {
/*    */   Object instance;
/*    */   Method method;
/*    */ 
/*    */   public BorrowedFunctor(Object i, String m)
/*    */   {
/*    */     try
/*    */     {
/* 13 */       instance = i;
/* 14 */       method = i.getClass().getMethod(m, new Class[] { String.class });
/*    */     }
/*    */     catch (Exception e)
/*    */     {
/* 18 */       throw new Error("Borrowed Functor getMethod failure");
/*    */     }
/*    */   }
/*    */ 
/*    */   public BorrowedFunctor(Object i, Method m)
/*    */   {
/* 24 */     instance = i;
/* 25 */     method = m;
/*    */   }
/*    */ 
/*    */   public String fire(String s) {
/* 29 */     Object[] arr = { s };
/*    */     try {
/* 31 */       return (String)method.invoke(instance, arr);
/*    */     }
/*    */     catch (IllegalAccessException e) {
/* 34 */       e.printStackTrace();
/*    */     }
/*    */     catch (InvocationTargetException e1) {
/* 37 */       e1.printStackTrace();
/*    */     }
/*    */ 
/* 40 */     return null;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.IOPipes.functor.BorrowedFunctor
 * JD-Core Version:    0.6.0
 */