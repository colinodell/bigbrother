package net.hcfactions.bigbrother.blocklogging.model;

import org.bukkit.block.Block;

public abstract class BlockAccess extends BlockInteraction {
    public String player;
    public long lastAccessed;
    public int accessCount;

    public BlockAccess(Block b) {
        super(b);
    }
}
