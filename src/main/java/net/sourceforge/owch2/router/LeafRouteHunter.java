/*    */ package net.sourceforge.owch2.router;
/*    */ 
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import net.sourceforge.owch2.kernel.Env;
/*    */ 
/*    */ public class LeafRouteHunter extends RouteHunterImpl
/*    */ {
/* 28 */   private static Collection outbound = Arrays.asList(Env.getRouter("IPC"), Env.getRouter("owch"), Env.getRouter("http"), Env.getRouter("default"));
/*    */ 
/* 30 */   private static Collection inbound = Arrays.asList(Env.getRouter("IPC"), Env.getRouter("owch"), Env.getRouter("http"));
/*    */ 
/*    */   public Collection getOutbound()
/*    */   {
/* 13 */     return outbound;
/*    */   }
/*    */ 
/*    */   public void setOutbound(Collection outbound) {
/* 17 */     outbound = outbound;
/*    */   }
/*    */ 
/*    */   public Collection getInbound() {
/* 21 */     return inbound;
/*    */   }
/*    */ 
/*    */   public void setInbound(Collection inbound) {
/* 25 */     inbound = inbound;
/*    */   }
/*    */ }

/* Location:           C:\Users\jim\Downloads\owch2.jar
 * Qualified Name:     net.sourceforge.owch2.router.LeafRouteHunter
 * JD-Core Version:    0.6.0
 */