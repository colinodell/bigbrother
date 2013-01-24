package net.hcfactions.bigbrother.blocklogging;

import net.hcfactions.bigbrother.BigBrotherPlugin;
import net.hcfactions.bigbrother.blocklogging.events.EventManager;
import net.hcfactions.bigbrother.blocklogging.model.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.sql.SQLException;

public class BlockEventListener implements Listener {
    private BigBrotherPlugin plugin;

    public BlockEventListener(BigBrotherPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(event.isCancelled())
            return;

        // Only log changes made by players
        if(event.getPlayer() == null)
            return;

        BlockChange record = (BlockChange) EventManager.createRecord(event);
        if(record != null)
            plugin.getBlockDbHelper().savePlacedBlock(record);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.isCancelled())
            return;

        // Only log changes made by players
        if(event.getPlayer() == null)
            return;

        BlockChange record = (BlockChange)EventManager.createRecord(event);

        if(record != null)
            plugin.getBlockDbHelper().saveDestroyedBlock(record);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSignChange(SignChangeEvent event)
    {
        if(event.isCancelled())
            return;

        // Only log changes made by players
        if(event.getPlayer() == null)
            return;

        BlockChange record = (BlockChange)EventManager.createRecord(event);

        if(record != null)
            plugin.getBlockDbHelper().savePlacedBlock(record);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event)
    {
        if(event.isCancelled())
            return;

        if(event.getPlayer() == null || event.getInventory() == null)
            return;

        ChestInventoryOpen record = (ChestInventoryOpen)EventManager.createRecord(event);

        if(record != null)
            plugin.getBlockDbHelper().saveChestAccess(record);
    }


}
