package net.hcfactions.bigbrother.blocklogging.model;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockChange extends BlockInteraction {
    public long datePlaced;
    public String placedBy;

    public long dateDestroyed;
    public String destroyedBy;

    protected BlockChange(Block b) {
        super(b);
    }

    protected BlockChange (BlockPlaceEvent event)
    {
        this(event.getBlock());

        if(event.getPlayer() != null)
            this.placedBy = event.getPlayer().getName();

        this.datePlaced = System.currentTimeMillis() / 1000L;

        this.destroyedBy = null;
        this.dateDestroyed = 0;
    }

    protected BlockChange (BlockBreakEvent event)
    {
        this(event.getBlock());

        if(event.getPlayer() != null)
            this.destroyedBy = event.getPlayer().getName();

        this.dateDestroyed = System.currentTimeMillis() / 1000L;
    }
}
