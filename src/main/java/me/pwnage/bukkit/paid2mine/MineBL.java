/*    */ package me.pwnage.bukkit.paid2mine;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import org.bukkit.block.Block;
/*    */ import org.bukkit.entity.Player;
/*    */ import org.bukkit.event.block.BlockBreakEvent;
/*    */ import org.bukkit.event.block.BlockListener;
/*    */ 
/*    */ public class MineBL extends BlockListener
/*    */ {
/*    */   private Paid2Mine plugin;
/*    */ 
/*    */   public MineBL(Paid2Mine plugin)
/*    */   {
/* 12 */     this.plugin = plugin;
/*    */   }
/*    */ 
/*    */   public void onBlockBreak(BlockBreakEvent event)
/*    */   {
/* 18 */     Block b = event.getBlock();
/*    */ 
/* 20 */     double value = Paid2Mine.defaultValue.doubleValue();
/* 21 */     if (Paid2Mine.ItemValues.containsKey(Integer.valueOf(b.getTypeId())))
/*    */     {
/* 23 */       value = ((Double)Paid2Mine.ItemValues.get(Integer.valueOf(b.getTypeId()))).doubleValue();
/*    */     }
/*    */ 
/* 26 */     this.plugin.addToQueue(event.getPlayer().getName(), Double.valueOf(value));
/*    */   }
/*    */ }