/*     */ package me.pwnage.bukkit.paid2mine;
/*     */ 
/*     */ import com.iConomy.iConomy;
/*     */ import java.io.File;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.event.Event;
/*     */ import org.bukkit.plugin.PluginDescriptionFile;
/*     */ import org.bukkit.plugin.PluginManager;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ import org.bukkit.scheduler.BukkitScheduler;
/*     */ import org.bukkit.util.config.Configuration;
/*     */ 
/*     */ public class Paid2Mine extends JavaPlugin
/*     */ {
/*  20 */   public static Logger log = Logger.getLogger("Minecraft");
/*     */   public String name;
/*     */   public String version;
/*     */   public iConomy icon;
/*     */   public MineBL minebl;
/*  25 */   public static boolean debug = false;
/*  26 */   public boolean alertPlayer = false;
/*  27 */   public String alertMessage = "";
/*     */   private static Configuration config;
/*  31 */   public static HashMap<Integer, Double> ItemValues = new HashMap();
/*  32 */   public static Double defaultValue = Double.valueOf(0.0D);
/*     */ 
/*  34 */   public static HashMap<String, Double> SQLCache = new HashMap();
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*     */     try
/*     */     {
/*  41 */       this.icon = ((iConomy)getServer().getPluginManager().getPlugin("iConomy"));
/*     */ 
/*  43 */       this.name = getDescription().getName();
/*  44 */       this.version = getDescription().getVersion();
/*     */     }
/*     */     catch (Exception e) {
/*  47 */       log.log(Level.INFO, "[" + this.name + "] iConomy not found, disabling...");
/*  48 */       getServer().getPluginManager().disablePlugin(this);
/*  49 */       return;
/*     */     }
/*     */ 
/*  52 */     getServer().getScheduler().scheduleSyncRepeatingTask(this, new iUpdate(this), 0L, 2500L);
/*     */ 
/*  54 */     readConfig();
/*     */ 
/*  56 */     this.minebl = new MineBL(this);
/*     */ 
/*  58 */     getServer().getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, this.minebl, Event.Priority.Normal, this);
/*  59 */     log.log(Level.INFO, "[" + this.name + "] Enabled.");
/*     */   }
/*     */ 
/*     */   public void readConfig()
/*     */   {
/*  64 */     File configLoc = new File("plugins/Paid2Mine/values.yml");
/*     */     try
/*     */     {
/*  68 */       if (!configLoc.exists())
/*     */       {
/*  70 */         new File("plugins/Paid2Mine/").mkdir();
/*  71 */         configLoc.createNewFile();
/*  72 */         config = new Configuration(configLoc);
/*  73 */         config.load();
/*     */ 
/*  75 */         config.setProperty("config.alert.enabled", "true");
/*  76 */         config.setProperty("config.alert.message", "It's payday! You have earned $$");
/*  77 */         config.setProperty("items.default.value", "0.10");
/*  78 */         config.setProperty("items.stone.itemid", "1");
/*  79 */         config.setProperty("items.stone.value", "0.10");
/*     */ 
/*  81 */         config.save();
/*     */       } else {
/*  83 */         config = new Configuration(configLoc);
/*  84 */         config.load();
/*     */       }
/*     */ 
/*  87 */       debug = config.getBoolean("debug.showinfo", false);
/*  88 */       this.alertPlayer = config.getBoolean("config.alert.enabled", true);
/*  89 */       this.alertMessage = config.getString("config.alert.message", "It's payday! You have earned $$");
/*  90 */       this.alertMessage = this.alertMessage.replaceAll("&", "¤");
/*  91 */       this.alertMessage = this.alertMessage.replaceAll("\\'", "'");
/*     */ 
/*  93 */       List<String> KeysToImport = config.getKeys("items");
/*     */ 
/*  95 */       ItemValues.clear();
/*     */ 
/*  97 */       for (String x : KeysToImport)
/*     */       {
/*  99 */         if (x.equalsIgnoreCase("default"))
/*     */         {
/* 101 */           defaultValue = config.getDouble("items." + x + ".value", 0.10);
/*     */         }
/* 103 */         else ItemValues.put(config.getInt("items." + x + ".itemid", 1), config.getDouble("items." + x + ".value", 0.1));
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 108 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void onDisable()
/*     */   {
/* 115 */     log.log(Level.INFO, "[" + this.name + "] Disabled.");
/*     */   }
/*     */ 
/*     */   public boolean onCommand(CommandSender cs, Command c, String com, String[] arg)
/*     */   {
/* 121 */     if (com.equalsIgnoreCase("minepay"))
/*     */     {
/* 123 */       if ((arg.length > 0) && (arg[0].equalsIgnoreCase("-reload")) && (cs.isOp()))
/*     */       {
/* 125 */         readConfig();
/* 126 */         cs.sendMessage(ChatColor.GREEN + "[" + this.name + "] Reloaded configuration.");
/*     */       }
/* 128 */       if ((arg.length > 0) && (arg[0].equalsIgnoreCase("-version")) && (cs.isOp()))
/*     */       {
/* 130 */         cs.sendMessage(ChatColor.GREEN + "[" + this.name + "] Plugin Version: " + getDescription().getVersion());
/*     */       }
/* 132 */       return true;
/*     */     }
/* 134 */     return false;
/*     */   }
/*     */ 
/*     */   public void addToQueue(String playerName, Double amount)
/*     */   {
/*     */     double amt;
/* 140 */     if (SQLCache.containsKey(playerName))
/*     */     {
/* 142 */       amt = ((Double)SQLCache.get(playerName)).doubleValue() + amount.doubleValue();
/* 143 */       SQLCache.remove(playerName);
/*     */     } else {
/* 145 */       amt = amount.doubleValue();
/*     */     }
/*     */ 
/* 148 */     SQLCache.put(playerName, Double.valueOf(amt));
/*     */   }
/*     */ }