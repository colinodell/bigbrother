package net.hcfactions.bigbrother.blocklogging;

import net.hcfactions.bigbrother.BigBrotherPlugin;
import net.hcfactions.bigbrother.blocklogging.model.BlockChange;
import net.hcfactions.bigbrother.blocklogging.model.PlayerItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.LinkedList;
import java.util.ListIterator;

public class DropRecorder implements Runnable {
    private BigBrotherPlugin plugin;

    public DropRecorder(BigBrotherPlugin plugin)
    {
        this.plugin = plugin;
    }

    private LinkedList<BlockChange> blockStates = new LinkedList<BlockChange>();

    public void recordBlockBreak(BlockBreakEvent event)
    {
        this.blockStates.add(new BlockChange(event));
    }

    public void onItemSpawned(Item item, BlockDbHelper helper)
    {
        // The getPickupDelay() should always be 10 for broken block drops.
        // We'll check against 20 to be safe, for whatever reason
        // I believe 40 is the default delay for thrown items
        if(item.getPickupDelay() > 20)
            return;

        ListIterator<BlockChange> i = blockStates.listIterator();
        while(i.hasNext())
        {
            BlockChange bi = i.next();
            if(!bi.world.equals(item.getLocation().getWorld().getName()))
                continue;

            // The drop item coordinates will always be within 0.15 - 0.85 units larger than the block coords (as of 1.3.2)
            if(item.getLocation().getX() - bi.xPos <= 1)
                if(item.getLocation().getY() - bi.yPos <= 1)
                    if(item.getLocation().getZ() - bi.zPos <= 1)
                        // The item dropped in the right spot, but just double-check that it's the right kind
                        if(materialMatches(bi.blockType, item.getItemStack().getType()))
                        {
                            // We matched the broken block with the item in this location
                            // Note that we don't remove the BlockChange. This is because multiple
                            // drops may not exist in a single stack - if we removed the BlockChange, we
                            // can't associate the extras with the existing block.
                            helper.updateOreCount(new PlayerItemStack(bi, item.getItemStack()));
                            return;
                        }
        }
    }

    /**
     * Match the block type with its corresponding drop type
     * @param blockType Type of ore block
     * @param dropType Type of item drop
     * @return Whether the given ore drop the given item
     */
    public boolean materialMatches(Material blockType, Material dropType)
    {
        if(blockType == Material.COAL_ORE && dropType == Material.COAL)
            return true;
        if(blockType == Material.GLOWING_REDSTONE_ORE && dropType == Material.REDSTONE)
            return true;
        if(blockType == Material.REDSTONE_ORE && dropType == Material.REDSTONE)
            return true;
        if(blockType == Material.DIAMOND_ORE && dropType == Material.DIAMOND)
            return true;
        if(blockType == Material.EMERALD_ORE && dropType == Material.EMERALD)
            return true;
        if(blockType == Material.LAPIS_ORE && dropType == Material.INK_SACK)
            return true;


        return false;
    }

    /**
     * Removes any BlockChange entity that's been around for 2 seconds or longer
     */
    public void run()
    {
        // Remove any blocks recorded more than 2 seconds old
        long twoSecondsAgo = (System.currentTimeMillis() / 1000L) - 2;
        ListIterator<BlockChange> i = blockStates.listIterator();
        while(i.hasNext())
        {
            if(i.next().dateDestroyed <= twoSecondsAgo)
                i.remove();
        }
    }
}
