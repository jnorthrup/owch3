/*    */ package net.sourceforge.nlp;
/*    */ 
/*    */ import java.io.FileInputStream;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.ObjectOutputStream;
/*    */ import java.util.Map;
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class WordStore extends VerbKnowledge
/*    */ {
/*  7 */   private Map wordMap = new TreeMap();
/*    */ 
/*    */   protected WordStore(String InputObjResource) throws Exception {
/* 10 */     read(InputObjResource);
/*    */   }
/*    */ 
/*    */   public Map getWordMap() {
/* 14 */     return wordMap;
/*    */   }
/*    */ 
/*    */   public void setWordMap(Map wordMap) {
/* 18 */     this.wordMap = wordMap;
/*    */   }
/*    */ 
/*    */   public void read(String InputObjResource) throws FileNotFoundException, IOException, ClassNotFoundException
/*    */   {
/* 23 */     ObjectInputStream is = new ObjectInputStream(new FileInputStream(InputObjResource));
/*    */ 
/* 25 */     Object[] w = (Object[])is.readObject();
/*    */ 
/* 27 */     metaClause = ((Map)w[0]);
/* 28 */     clauseVerb= ((Map)w[1]);
/* 29 */     if (w.length < 3) {
/* 30 */       wordMap = new TreeMap();
/*    */     }
/*    */     else
/* 33 */       wordMap = ((Map)w[2]);
/*    */   }
/*    */ 
/*    */   public void write(String argv)
/*    */   {
/*    */     try
/*    */     {
/* 41 */       ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(argv));
/*    */ 
/* 43 */       Object[] w = { metaClause, clauseVerb, wordMap };
/*    */ 
/* 49 */       os.writeObject(w);
/*    */     }
/*    */     catch (Exception e) {
/* 52 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.nlp.WordStore
 * JD-Core Version:    0.6.0
 */