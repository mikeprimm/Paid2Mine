package me.pwnage.bukkit.paid2mine;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import com.nijiko.coelho.iConomy.iConomy;
import com.nijiko.coelho.iConomy.system.Bank;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.util.config.Configuration;

public class Paid2Mine extends JavaPlugin
{
    public static Logger log = Logger.getLogger("Minecraft");
    public String name;
    public String version;
    public static iConomy icon;
    public MineBL minebl;
    public static boolean debug = false;

    private static Configuration config;

    public static HashMap<Integer, Double> ItemValues = new HashMap<Integer, Double>();
    public static Double defaultValue = 0.00;

    public static HashMap<String, Double> SQLCache = new HashMap<String, Double>();

    @Override
    public void onEnable()
    {
        try
        {
            icon = (iConomy)getServer().getPluginManager().getPlugin("iConomy");

            name = this.getDescription().getName();
            version = this.getDescription().getVersion();

            log.log(Level.INFO, "[" + name + "] Enabled.");
        } catch(Exception e)
        {
            log.log(Level.INFO, "[" + name + "] iConomy not found, disabling...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getScheduler().scheduleSyncRepeatingTask(this, new iUpdate(this), 0l, 1000L);

        readConfig();
        
        minebl = new MineBL(this);

        getServer().getPluginManager().registerEvent(Type.BLOCK_BREAK, minebl, Priority.Normal, this);
        log.log(Level.INFO, "[" + name + "] Enabled.");
    }

    public void readConfig()
    {
        File configLoc = new File("plugins/Paid2Mine/values.yml");

        try
        {
            if(!configLoc.exists())
            {
                new File("plugins/Paid2Mine/").mkdir();
                configLoc.createNewFile();
                config = new Configuration(configLoc);
                config.load();

                config.setProperty("items.default.value", "0.10");
                config.setProperty("items.stone.itemid", "1");
                config.setProperty("items.stone.value", "0.10");

                config.save();
            } else {
                config = new Configuration(configLoc);
                config.load();
            }

            debug = Boolean.parseBoolean((String)config.getProperty("debug.showinfo"));

            List<String> KeysToImport = config.getKeys("items");

            ItemValues.clear();

            for(String x : KeysToImport)
            {
                if(x.equalsIgnoreCase("default"))
                {
                    defaultValue = Double.parseDouble((String)config.getProperty("items." + x + ".value"));
                } else {
                    ItemValues.put(Integer.parseInt((String)config.getProperty("items." + x + ".itemid")), Double.parseDouble((String)config.getProperty("items." + x + ".value")));
                }
            }
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable()
    {
        log.log(Level.INFO, "[" + name + "] Disabled.");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command c, String com, String[] arg)
    {
        if(com.equalsIgnoreCase("minepay"))
        {
            if(arg.length > 0 && arg[0].equalsIgnoreCase("-reload") && cs.isOp())
            {
                readConfig();
                cs.sendMessage(ChatColor.GREEN + "[" + name + "] Reloaded configuration.");
            }
            if(arg.length > 0 && arg[0].equalsIgnoreCase("-version") && cs.isOp())
            {
                cs.sendMessage(ChatColor.GREEN + "[" + name + "] Plugin Version: " + this.getDescription().getVersion());
            }
            return true;
        }
        return false;
    }

    public void addToQueue(String playerName, Double amount)
    {
        double amt;
        if(SQLCache.containsKey(playerName))
        {
            amt = SQLCache.get(playerName) + amount;
            SQLCache.remove(playerName);
        } else {
            amt = amount;
        }

        SQLCache.put(playerName, amt);
    }
}

class iUpdate implements Runnable
{
    Paid2Mine plugin;
    public iUpdate(Paid2Mine pl)
    {
        this.plugin = pl;
    }

    public void run()
    {
        Bank b = plugin.icon.getBank();
        for(Object x : plugin.SQLCache.keySet().toArray())
        {
            b.getAccount(x).add(plugin.SQLCache.get((String)x));
        }
    }
}
