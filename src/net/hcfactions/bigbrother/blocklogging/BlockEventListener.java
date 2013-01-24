package net.hcfactions.bigbrother.blocklogging;

import net.hcfactions.bigbrother.BigBrotherPlugin;
import net.hcfactions.bigbrother.blocklogging.events.EventManager;
import net.hcfactions.bigbrother.blocklogging.model.*;
import net.hcfactions.core.BasePlugin;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

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

        BaseModel record = (BaseModel)EventManager.createRecord(event);

        if(record != null)
        {
            if(record instanceof OreBreak)
            {
                // Record the block break event
                // Once it drops the items, we'll use this info to match the two
                // and identify/credit the player accordingly
                plugin.getDropRecorer().recordBlockBreak(event);
            }
            else if(record instanceof BlockChange)
            {
                plugin.getBlockDbHelper().saveDestroyedBlock((BlockChange)record);
            }
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event)
    {
        if(event.isCancelled())
            return;

        if(event.getWhoClicked() == null || event.getWhoClicked().getName() == null)
            return;

        PlayerItemStack record = (PlayerItemStack)EventManager.createRecord(event);

        if(record != null)
        {
            plugin.getBlockDbHelper().updateOreCount(record);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDrop(ItemSpawnEvent event)
    {
        if(event.getEntity().getPickupDelay() < 20)
        {
            switch(event.getEntity().getItemStack().getType())
            {
                case INK_SACK:
                    byte b = event.getEntity().getItemStack().getData().getData();
                    // When b == 4, the "ink sack" is really lapis
                    // So if it's not 4, abort
                    if(b != 4)
                        return;
                case DIAMOND:
                case EMERALD:
                case COAL:
                case REDSTONE:
                    // Record the item spawn event
                    // The DropRecorder will look for any just-broken blocks in that spot and, if found,
                    // will credit the player for the drops by calling the relevant method on the BlockDbHelper
                    plugin.getDropRecorer().onItemSpawned(event.getEntity(), plugin.getBlockDbHelper());
                    return;
            }
        }
    }

}
