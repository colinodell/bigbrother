package net.hcfactions.bigbrother;

import net.hcfactions.bigbrother.events.EventManager;
import net.hcfactions.bigbrother.model.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.sql.SQLException;

public class BlockChangeListener implements Listener {
    private BigBrotherPlugin plugin;
    private DbHelper db;

    public BlockChangeListener(BigBrotherPlugin plugin)
    {
        this.plugin = plugin;
        this.db = new DbHelper(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if(event.isCancelled())
            return;

        // Only log changes made by players
        if(event.getPlayer() == null)
            return;

        try
        {
            BlockChange record = (BlockChange) EventManager.createRecord(event);
            if(record != null)
                db.savePlacedBlock(record);
        }
        catch(SQLException ex) {
            plugin.getLogger().warning(ex.getMessage());
        }

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event)
    {
        if(event.isCancelled())
            return;

        // Only log changes made by players
        if(event.getPlayer() == null)
            return;

        try
        {
            BlockChange record = (BlockChange)EventManager.createRecord(event);

            if(record != null)
                db.saveDestroyedBlock(record);
        }
        catch(SQLException ex) {

            plugin.getLogger().warning(ex.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSignChange(SignChangeEvent event)
    {
        if(event.isCancelled())
            return;

        // Only log changes made by players
        if(event.getPlayer() == null)
            return;

        try
        {
            BlockChange record = (BlockChange)EventManager.createRecord(event);

            if(record != null)
                db.savePlacedBlock(record);
        }
        catch(SQLException ex) {
            plugin.getLogger().warning(ex.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event)
    {
        if(event.isCancelled())
            return;

        if(event.getPlayer() == null || event.getInventory() == null)
            return;

        try
        {
            ChestInventoryOpen record = (ChestInventoryOpen)EventManager.createRecord(event);

            if(record != null)
                db.saveChestAccess(record);

        }
        catch(SQLException ex) {
            plugin.getLogger().warning(ex.getMessage());
        }
    }


}
