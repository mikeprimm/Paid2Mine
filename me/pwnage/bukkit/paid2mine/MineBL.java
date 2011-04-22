package me.pwnage.bukkit.paid2mine;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class MineBL extends BlockListener
{
    private Paid2Mine plugin;
    public MineBL(Paid2Mine plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event)
    {
        Block b = event.getBlock();
        
        double value = plugin.defaultValue;
        if(plugin.ItemValues.containsKey(b.getTypeId()))
        {
            value = plugin.ItemValues.get(b.getTypeId());
        }

        plugin.addToQueue(event.getPlayer().getName(), value);
    }
}
