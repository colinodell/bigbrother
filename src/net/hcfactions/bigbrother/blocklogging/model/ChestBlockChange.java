package net.hcfactions.bigbrother.blocklogging.model;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Represents a chest block that was created or destroyed
 */
public class ChestBlockChange extends BlockChange{

    protected ChestBlockChange(Block b)
    {
        super(b);

        // Use the left side for referencing double-chests
        if(b instanceof DoubleChest)
        {
            Chest leftChest = (Chest) ((DoubleChest) b).getLeftSide();
            this.xPos = leftChest.getX();
            this.yPos = leftChest.getY();
            this.zPos = leftChest.getZ();
        }
    }

    public ChestBlockChange (BlockPlaceEvent event)
    {
        super(event);
    }

    public ChestBlockChange (BlockBreakEvent event)
    {
        super(event);
    }
}
