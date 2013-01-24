package net.hcfactions.bigbrother.blocklogging.model;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class ChestBlockChange extends BlockChange{

    protected ChestBlockChange(Block b)
    {
        super(b);

        /* I don't know if we need this, but I'm keeping the code here for reference just in case
        // Use the left side for referencing double-chests
        if(b instanceof DoubleChest)
        {
            Chest leftChest = (Chest) ((DoubleChest) b).getLeftSide();
            this.xPos = leftChest.getX();
            this.yPos = leftChest.getY();
            this.zPos = leftChest.getZ();
        }*/
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
