package net.hcfactions.bigbrother.blocklogging.model;

import org.bukkit.block.Block;

/**
 * Represents a block which was accessed or used by a player
 */
public abstract class BlockAccess extends BlockInteraction {
    public String player;
    public long lastAccessed;
    public int accessCount;

    public BlockAccess(Block b) {
        super(b);
    }
}
