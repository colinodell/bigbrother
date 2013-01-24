package net.hcfactions.bigbrother.model;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

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
        InventoryHolder holder = event.getInventory().getHolder();
        if(holder instanceof Chest)
            return (Chest) holder;
        else if(holder instanceof DoubleChest)
            return (Chest)((DoubleChest)holder).getLeftSide();

        return null;
    }
}
