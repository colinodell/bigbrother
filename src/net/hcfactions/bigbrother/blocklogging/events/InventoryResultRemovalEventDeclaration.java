package net.hcfactions.bigbrother.blocklogging.events;

import net.hcfactions.bigbrother.blocklogging.model.BlockInteraction;
import net.hcfactions.bigbrother.blocklogging.model.PlayerItemStack;
import net.hcfactions.core.BasePlugin;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryResultRemovalEventDeclaration extends InventoryEventDeclaration {

    protected List<Material> supportedResultMaterials = new ArrayList<Material>();

    private InventoryResultRemovalEventDeclaration(Class<? extends Event> eventType, Class<? extends BlockInteraction> model, InventoryType type) {
        super(eventType, model, type);
    }

    private InventoryResultRemovalEventDeclaration(Class<? extends Event> eventType, Class<? extends BlockInteraction> model, InventoryType... types) {
        super(eventType, model, types);
    }

    public InventoryResultRemovalEventDeclaration(Class<? extends InventoryClickEvent> eventType, Class<? extends PlayerItemStack> model, InventoryType invtype,  Material... types)
    {
        super(eventType, model, invtype);
        supportedResultMaterials = new ArrayList<Material>(Arrays.asList(types));
    }

    public boolean shouldHandleEvent(Event event)
    {
        if(!super.shouldHandleEvent(event))
            return false;

        InventoryClickEvent e = (InventoryClickEvent)event;

        // We only care about the result slot
        if(e.getSlotType() != InventoryType.SlotType.RESULT)
            return false;

        // This should probably never happen, but you never know with Java. If it does, abort.
        if(e.getCurrentItem() == null || e.getCurrentItem().getType() == null)
            return false;

        // Make sure something was actually removed (technically, "clicked on")
        ItemStack smelted = e.getCurrentItem();
        int qty = smelted.getAmount();
        if(qty == 0)
            return false;

        // At this point, the result represents the possible removal of a smelted item from a furnace

        // However, depending on what was in your hand, it's possible that nothing was actually removed
        // 1. Left  click, without shift, holding a different item (non-air) causes NOTHING to be removed
        // 2. Right click, without shift, holding a different item (non-air) causes NOTHING to be removed
        if(!e.isShiftClick())
        {
            // The cursor must be air or the same material type, otherwise nothing gets removed
            if(e.getCursor().getType() != Material.AIR && e.getCursor().getType() != e.getCurrentItem().getType())
                return false;
        }

        // Lastly, check the types
        // If no resulting material types (i.e. iron ingots) are listed, assume this applies to any/all; otherwise check the list
        if(this.supportedResultMaterials.size() == 0)
            return true;

        if(this.supportedResultMaterials.contains(e.getCurrentItem().getType()))
            return true;

        return false;
    }
}
