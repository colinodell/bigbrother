package net.hcfactions.bigbrother.blocklogging.model;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Represents a Sign whose text was modified
 */
public class SignBlockChange extends BlockChange {

    public String[] lines;

    protected SignBlockChange(Block b)
    {
        super(b);
    }

    public SignBlockChange (BlockPlaceEvent event)
    {
        super(event);
    }

    public SignBlockChange (BlockBreakEvent event)
    {
        super(event);
    }

    public SignBlockChange (SignChangeEvent event)
    {
        super(event.getBlock());

        if(event.getPlayer() != null)
            this.placedBy = event.getPlayer().getName();

        this.datePlaced = System.currentTimeMillis() / 1000L;

        this.destroyedBy = null;
        this.dateDestroyed = 0;

        this.lines = event.getLines();
    }

}
