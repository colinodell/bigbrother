package net.hcfactions.bigbrother.blocklogging.model;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class OreBreak extends BlockChange {
    private OreBreak(Block b) {
        super(b);
    }

    private OreBreak(BlockPlaceEvent event) {
        super(event);
    }

    public OreBreak(BlockBreakEvent event) {
        super(event);
    }
}
