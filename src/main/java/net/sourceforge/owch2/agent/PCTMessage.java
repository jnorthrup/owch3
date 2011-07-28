/*    */ package net.sourceforge.owch2.agent;
/*    */ 
/*    */ public class PCTMessage
/*    */ {
/*    */   private command cmd_type;
/*    */   private char arg;
/*    */   private char flags;
/*    */   private String sn;
/*    */   private String data;
/*    */ 
/*    */   public PCTMessage(command cmd_type, char arg, char flags, String sn, String data)
/*    */   {
/* 19 */     setCmd_type(cmd_type);
/* 20 */     setArg(arg);
/* 21 */     setFlags(flags);
/* 22 */     setSn(sn);
/* 23 */     setData(data);
/*    */   }
/*    */ 
/*    */   public command getCmd_type()
/*    */   {
/* 28 */     return cmd_type;
/*    */   }
/*    */ 
/*    */   public void setCmd_type(command cmd_type) {
/* 32 */     this.cmd_type = cmd_type;
/*    */   }
/*    */ 
/*    */   public char getArg() {
/* 36 */     return arg;
/*    */   }
/*    */ 
/*    */   public void setArg(char arg) {
/* 40 */     this.arg = arg;
/*    */   }
/*    */ 
/*    */   public char getFlags() {
/* 44 */     return flags;
/*    */   }
/*    */ 
/*    */   public void setFlags(char flags) {
/* 48 */     this.flags = flags;
/*    */   }
/*    */ 
/*    */   public String getSn() {
/* 52 */     return sn;
/*    */   }
/*    */ 
/*    */   public void setSn(String sn) {
/* 56 */     this.sn = sn;
/*    */   }
/*    */ 
/*    */   public String getData() {
/* 60 */     return data;
/*    */   }
/*    */ 
/*    */   public void setData(String data) {
/* 64 */     this.data = data;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.agent.PCTMessage
 * JD-Core Version:    0.6.0
 */