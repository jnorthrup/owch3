/*    */ package net.sourceforge.nlp;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.StringTokenizer;
/*    */ 
/*    */ public class SentenceParser extends WordStore
/*    */ {
/*    */   public SentenceParser(String InputObjResource)
/*    */     throws Exception
/*    */   {
/*  7 */     super(InputObjResource);
/*    */   }
/*    */ 
/*    */   public List tokenize(String line)
/*    */   {
/* 12 */     List ret = new ArrayList();
/* 13 */     StringTokenizer st = new StringTokenizer(line, ":,;'/ ");
/*    */ 
/* 15 */     while (st.hasMoreTokens()) {
/* 16 */       String t = st.nextToken();
/* 17 */       Map wm = getWordMap();
/* 18 */       Occurence w = (Occurence)wm.get(t);
/*    */ 
/* 20 */       if (w == null) {
/* 21 */         wm.put(t, w = new Occurence());
/*    */       }
/*    */       else {
/* 24 */         w.incr();
/*    */       }
/* 26 */       ret.add(new Report(t, w));
/*    */     }
/* 28 */     return ret;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.nlp.SentenceParser
 * JD-Core Version:    0.6.0
 */