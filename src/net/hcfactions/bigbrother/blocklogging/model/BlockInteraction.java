package net.hcfactions.bigbrother.blocklogging.model;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Represents a block within a world
 */
public abstract class BlockInteraction extends BaseModel {
    public String world;
    public int xPos;
    public int yPos;
    public int zPos;

    public Material blockType;

    public BlockInteraction(Block b)
    {
        this.blockType = b.getType();

        this.world = b.getWorld().getName();
        this.xPos = b.getX();
        this.yPos = b.getY();
        this.zPos = b.getZ();
    }

    public BlockInteraction(int x, int y, int z, String world, Material type)
    {
        this.blockType = type;

        this.world = world;
        this.xPos = x;
        this.yPos = y;
        this.zPos = z;
    }
}
