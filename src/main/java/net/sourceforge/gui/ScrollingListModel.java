/*    */ package net.sourceforge.gui;
/*    */ 
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import javax.swing.AbstractListModel;
/*    */ 
/*    */ public class ScrollingListModel extends AbstractListModel
/*    */ {
/*  7 */   List list = new LinkedList();
/*    */ 
/*    */   public void addElement(Object o) {
/* 10 */     list.add(o);
/* 11 */     super.fireIntervalAdded(this, list.size(), list.size());
/*    */   }
/*    */ 
/*    */   public Object getElementAt(int par1)
/*    */   {
/* 20 */     return list.get(par1);
/*    */   }
/*    */ 
/*    */   public int getSize()
/*    */   {
/* 25 */     return list.size();
/*    */   }
/*    */ 
/*    */   public void remove(int i)
/*    */   {
/* 30 */     list.remove(i);
/* 31 */     super.fireIntervalRemoved(this, i, i);
/*    */   }
/*    */ 
/*    */   public boolean remove(Object o)
/*    */   {
/* 36 */     super.fireIntervalRemoved(this, 0, 0);
/* 37 */     return list.remove(o);
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.gui.ScrollingListModel
 * JD-Core Version:    0.6.0
 */