/*     */ package me.pwnage.bukkit.paid2mine;
/*     */ 
/*     */ import com.iConomy.iConomy;
/*     */ import com.iConomy.system.Account;
/*     */ import com.iConomy.system.Holdings;
/*     */ import java.util.HashMap;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.entity.Player;
/*     */ 
/*     */ class iUpdate
/*     */   implements Runnable
/*     */ {
/*     */   private Paid2Mine plugin;
/*     */ 
/*     */   public iUpdate(Paid2Mine pl)
/*     */   {
/* 157 */     this.plugin = pl;
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 162 */     for (String x : Paid2Mine.SQLCache.keySet())
/*     */     {
/* 164 */       if (!x.equals(""))
/*     */       {
/* 166 */         iConomy.getAccount(x).getHoldings().add(((Double)Paid2Mine.SQLCache.get(x)).doubleValue());
/*     */ 
/* 168 */         if (this.plugin.alertPlayer)
/*     */         {
/* 170 */           this.plugin.getServer().getPlayer(x).sendMessage(this.plugin.alertMessage.replace("$$", "" + String.format("%.2f", new Object[] { Paid2Mine.SQLCache.get(x) })));
/*     */         }
/*     */       }
/*     */     }
/* 174 */     Paid2Mine.SQLCache.clear();
/*     */   }
/*     */ }

/* Location:           G:\Users\Mike\Downloads\Paid2Mine.jar
 * Qualified Name:     me.pwnage.bukkit.paid2mine.iUpdate
 * JD-Core Version:    0.6.0
 */