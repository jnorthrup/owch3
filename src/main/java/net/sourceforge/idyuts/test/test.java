/*    */ package net.sourceforge.idyuts.test;
/*    */ 
/*    */ import net.sourceforge.idyuts.IOConversion.ArrayCollectionConverter;
/*    */ import net.sourceforge.idyuts.IOConversion.IntStringConverter;
/*    */ import net.sourceforge.idyuts.IOLayer.ArrayFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.CollectionFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.StringFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.intFilter;
/*    */ import net.sourceforge.idyuts.IOLayer.intSource;
/*    */ import net.sourceforge.idyuts.IOPipes.RangeCounter;
/*    */ import net.sourceforge.idyuts.IOPipes.StringAssembler;
/*    */ import net.sourceforge.idyuts.IOUtil.Auto;
/*    */ 
/*    */ class test
/*    */ {
/*    */   public static void main(String[] args)
/*    */   {
/* 13 */     intSource isrc = new RangeCounter(25, 30);
/* 14 */     intFilter iflt = new IntPrinter();
/*    */ 
/* 17 */     Auto.attach(isrc, iflt);
/* 18 */     isrc.xmit();
/* 19 */     isrc = new RangeCounter(0, 5);
/*    */ 
/* 21 */     intFilter icnv = new IntStringConverter();
/*    */ 
/* 23 */     StringFilter sflt = new StringAssembler(4);
/*    */ 
/* 27 */     ArrayFilter acnv = new ArrayCollectionConverter();
/*    */ 
/* 29 */     CollectionFilter cflt = new CollectionPrinter();
/*    */ 
/* 33 */     Auto.attach(isrc, sflt);
/*    */ 
/* 35 */     Auto.attach(isrc, icnv);
/*    */ 
/* 37 */     Auto.attach(icnv, sflt);
/* 38 */     Auto.attach(sflt, acnv);
/* 39 */     Auto.attach(acnv, cflt);
/* 40 */     isrc.xmit();
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.idyuts.test.test
 * JD-Core Version:    0.6.0
 */