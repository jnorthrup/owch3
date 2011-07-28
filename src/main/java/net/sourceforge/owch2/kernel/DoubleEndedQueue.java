/*    */ package net.sourceforge.owch2.kernel;
/*    */ 
/*    */ public final class DoubleEndedQueue
/*    */ {
/*    */   DoubleEndedQueueNode head;
/*    */   DoubleEndedQueueNode tail;
/*    */ 
/*    */   public final synchronized void push(Object item)
/*    */   {
/* 22 */     head = new DoubleEndedQueueNode(head, item);
/* 23 */     if (tail == null)
/* 24 */       tail = head;
/*    */   }
/*    */ 
/*    */   public final Object pop()
/*    */   {
/* 30 */     if (tail != null) {
/* 31 */       DoubleEndedQueueNode td = tail;
/* 32 */       tail = tail.next;
/* 33 */       if (td == head) {
/* 34 */         head = null;
/*    */       }
/* 36 */       return td.getObject();
/*    */     }
/*    */ 
/* 39 */     return null;
/*    */   }
/*    */ 
/*    */   public final Object peek()
/*    */   {
/* 44 */     DoubleEndedQueueNode de = tail;
/* 45 */     if (de == null) {
/* 46 */       return null;
/*    */     }
/* 48 */     return de.getObject();
/*    */   }
/*    */ 
/*    */   public final boolean empty()
/*    */   {
/* 53 */     return head == null; } 
/* 62 */   final class DoubleEndedQueueNode { DoubleEndedQueueNode next = null;
/*    */     Object object;
/*    */ 
/* 58 */     DoubleEndedQueueNode(DoubleEndedQueueNode previous, Object o) { object = o;
/* 59 */       next = previous;
/*    */     }
/*    */ 
/*    */     final Object getObject()
/*    */     {
/* 65 */       return object;
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.kernel.DoubleEndedQueue
 * JD-Core Version:    0.6.0
 */