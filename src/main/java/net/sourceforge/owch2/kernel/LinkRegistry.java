/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ import java.util.TreeMap;
/*    */ 
/*    */ public class LinkRegistry extends TreeMap
/*    */ {
/* 42 */   static String[] reserved = { "ACK".intern(), "Created".intern(), "JMSDestination".intern(), "MessageText".intern(), "JMSReplyTo".intern(), "ResentFrom".intern(), "JMSMessageID".intern(), "JMSType".intern(), "URL".intern(), "retry".intern() };
/*    */ 
/*    */   public LinkRegistry()
/*    */   {
/*    */   }
/*    */ 
/*    */   public LinkRegistry(MetaProperties m)
/*    */   {
/* 26 */     super(m);
/* 27 */     prune();
/*    */   }
/*    */ 
/*    */   final void prune()
/*    */   {
/* 32 */     for (int i = 0; i < reserved.length; i++) {
/* 33 */       String t = reserved[i];
/* 34 */       if (containsKey(t))
/* 35 */         remove(t);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.LinkRegistry
 * JD-Core Version:    0.6.0
 */