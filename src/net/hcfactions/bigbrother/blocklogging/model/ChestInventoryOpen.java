package net.hcfactions.bigbrother.blocklogging.model;

import net.hcfactions.bigbrother.BigBrotherPlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Represents a Chest block that was opened
 */
public class ChestInventoryOpen extends BlockAccess {

    protected ChestInventoryOpen(Block b, HumanEntity p)
    {
        super(b);

        if(p != null)
            this.player = p.getName();

        this.lastAccessed = System.currentTimeMillis() / 1000L;
        this.accessCount = 1;
    }

    public ChestInventoryOpen(Chest c, HumanEntity p)
    {
        this(c.getBlock(), p);
    }

    public ChestInventoryOpen(InventoryOpenEvent event)
    {
        this(getChest(event), event.getPlayer());
    }

    protected static Chest getChest(InventoryOpenEvent event)
    {
        // So here's a fun fact:
        // Although this event is triggered by ender chests, they don't have a holder
        // Therefore we can't obtain a block to know the xyz coords
        // Only way to get that is monitoring all right-clicks :-/
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof Chest)
            return (Chest) holder;
        else if(holder instanceof DoubleChest)
            return (Chest)((DoubleChest)holder).getLeftSide();



        return null;
    }
}
