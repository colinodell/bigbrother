package net.hcfactions.bigbrother.blocklogging.events;

import net.hcfactions.bigbrother.BigBrotherPlugin;
import net.hcfactions.bigbrother.blocklogging.model.BaseModel;
import net.hcfactions.bigbrother.blocklogging.model.BlockInteraction;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An EventDeclaration that also filters based on the type of inventory opened
 */
public class InventoryEventDeclaration extends EventDeclaration {

    protected List<InventoryType> supportedTypes = new ArrayList<InventoryType>();

    public InventoryEventDeclaration(Class<? extends Event> eventType, Class<? extends BaseModel> model, InventoryType type)
    {
        super(eventType, model);
        this.supportedTypes = new ArrayList<InventoryType>();
        this.supportedTypes.add(type);
    }

    public InventoryEventDeclaration(Class<? extends Event> eventType, Class<? extends BaseModel> model, InventoryType... types)
    {
        super(eventType, model);
        this.supportedTypes = new ArrayList<InventoryType>(Arrays.asList(types));
    }

    public boolean shouldHandleEvent(InventoryEvent e)
    {
        // Call base logic, which just compares the event classes
        if(!super.shouldHandleEvent(e))
            return false;

        // If no holder types (i.e. chest) are listed, assume this applies to any/all
        if(this.supportedTypes.size() == 0)
            return true;

        InventoryEvent event = (InventoryEvent)e;

        // This should probably never happen, but you never know with Java. If it does, abort.
        if(event.getInventory() == null || event.getInventory().getType() == null)
            return false;

        // Check if this holder type is supported
        return this.supportedTypes.contains(event.getInventory().getType());
    }
}
