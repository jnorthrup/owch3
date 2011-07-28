/*    */ package net.sourceforge.gui;
/*    */ 
/*    */ import java.awt.FlowLayout;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTextField;
/*    */ import javax.swing.text.JTextComponent;
/*    */ 
/*    */ public class TextPanel extends JPanel
/*    */ {
/*    */   private JLabel label;
/*    */   private JTextComponent textField;
/*    */ 
/*    */   public TextPanel(String name)
/*    */   {
/* 22 */     super(new FlowLayout());
/*    */ 
/* 24 */     setLabel(new JLabel(name));
/* 25 */     setTextField(new JTextField());
/* 26 */     getLabel().setLabelFor(getTextField());
/* 27 */     add(getLabel());
/* 28 */     add(getTextField());
/*    */   }
/*    */ 
/*    */   public JLabel getLabel() {
/* 32 */     return label;
/*    */   }
/*    */ 
/*    */   public void setColumns(int c) {
/* 36 */     ((JTextField)getTextField()).setColumns(c);
/*    */   }
/*    */ 
/*    */   public void setLabel(JLabel label) {
/* 40 */     this.label = label;
/*    */   }
/*    */ 
/*    */   public JTextComponent getTextField() {
/* 44 */     return textField;
/*    */   }
/*    */ 
/*    */   public void setTextField(JTextComponent textField) {
/* 48 */     this.textField = textField;
/*    */   }
/*    */ 
/*    */   public String getText() {
/* 52 */     return getTextField().getText();
/*    */   }
/*    */ 
/*    */   public void setText(String text) {
/* 56 */     getTextField().setText(text);
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.gui.TextPanel
 * JD-Core Version:    0.6.0
 */