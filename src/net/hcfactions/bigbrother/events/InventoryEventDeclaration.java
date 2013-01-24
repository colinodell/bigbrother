package net.hcfactions.bigbrother.events;

import net.hcfactions.bigbrother.model.BlockInteraction;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryEventDeclaration extends EventDeclaration {

    protected List<Class<? extends InventoryHolder>> supportedHolders = new ArrayList<Class<? extends InventoryHolder>>();

    public InventoryEventDeclaration(Class<? extends Event> eventType, Class<? extends BlockInteraction> model, Class<? extends InventoryHolder> holder)
    {
        super(eventType, model);
        this.supportedHolders = new ArrayList<Class<? extends InventoryHolder>>();
        this.supportedHolders.add(holder);
    }

    public InventoryEventDeclaration(Class<? extends Event> eventType, Class<? extends BlockInteraction> model, Class<? extends InventoryHolder>... holders)
    {
        super(eventType, model);
        this.supportedHolders = new ArrayList<Class<? extends InventoryHolder>>(Arrays.asList(holders));
    }

    public boolean shouldHandleEvent(InventoryEvent e)
    {
        // Call base logic, which just compares the event classes
        if(!super.shouldHandleEvent(e))
            return false;

        // If no holder types (i.e. chest) are listed, assume this applies to any/all
        if(this.supportedHolders.size() == 0)
            return true;

        InventoryEvent event = (InventoryEvent)e;

        // This should probably never happen, but you never know with Java. If it does, abort.
        if(event.getInventory() == null || event.getInventory().getHolder() == null)
            return false;

        // Check if this holder type is supported
        for(Class<? extends InventoryHolder> cls : this.supportedHolders)
        {
            if(cls.isInstance(event.getInventory().getHolder()))
                return true;
        }

        // We don't handle this type of block
        return false;
    }
}
