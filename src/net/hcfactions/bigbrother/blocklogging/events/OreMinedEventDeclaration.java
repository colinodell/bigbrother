package net.hcfactions.bigbrother.blocklogging.events;

import net.hcfactions.bigbrother.blocklogging.model.BaseModel;
import net.hcfactions.bigbrother.blocklogging.model.BlockInteraction;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class OreMinedEventDeclaration extends BlockEventDeclaration {
    public OreMinedEventDeclaration(Class<? extends BlockBreakEvent> eventType, Class<? extends BaseModel> model, Material material) {
        super(eventType, model, material);
    }

    public OreMinedEventDeclaration(Class<? extends BlockBreakEvent> eventType, Class<? extends BaseModel> model, Material... materials) {
        super(eventType, model, materials);
    }

    public boolean shouldHandleEvent(Event event)
    {
        if(!super.shouldHandleEvent(event))
            return false;

        BlockBreakEvent e = (BlockBreakEvent)event;

        // Additional null checks
        if(e.getPlayer() == null || e.getPlayer().getItemInHand() == null || e.getBlock() == null || e.getBlock().getDrops() == null || e.getBlock().getDrops().size() == 0)
            return false;

        // Silk touch doesn't cause ores to drop anything
        ItemStack tool = e.getPlayer().getItemInHand();
        if(tool.containsEnchantment(Enchantment.SILK_TOUCH))
            return false;

        return true;
    }
}
