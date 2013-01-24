package net.hcfactions.bigbrother.events;

import net.hcfactions.bigbrother.model.BlockInteraction;
import net.hcfactions.bigbrother.model.ChestBlockChange;
import net.hcfactions.bigbrother.model.ChestInventoryOpen;
import net.hcfactions.bigbrother.model.SignBlockChange;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventManager {
    private static EventManager instance = null;
    private HashMap<String, ArrayList<EventDeclaration>> registry = new HashMap<String, ArrayList<EventDeclaration>>();

    private EventManager()
    {
        this.register(new BlockEventDeclaration(BlockPlaceEvent.class, ChestBlockChange.class, Material.CHEST));
        this.register(new BlockEventDeclaration(BlockPlaceEvent.class, SignBlockChange.class, Material.SIGN_POST, Material.WALL_SIGN));

        this.register(new BlockEventDeclaration(BlockBreakEvent.class, ChestBlockChange.class, Material.CHEST));
        this.register(new BlockEventDeclaration(BlockBreakEvent.class, SignBlockChange.class, Material.SIGN_POST, Material.WALL_SIGN));

        this.register(new EventDeclaration(SignChangeEvent.class, SignBlockChange.class));

        this.register(new InventoryEventDeclaration(InventoryOpenEvent.class, ChestInventoryOpen.class, Chest.class, DoubleChest.class));
    }

    // Singleton pattern
    private static EventManager getInstance()
    {
        if(instance == null)
        {
            instance = new EventManager();
        }
        return instance;
    }

    private void register(EventDeclaration ed)
    {
        String eventClassName = ed.getEventType().getSimpleName();
        ArrayList<EventDeclaration> declarations = new ArrayList<EventDeclaration>();

        if(registry.containsKey(eventClassName))
            declarations = registry.get(eventClassName);

        declarations.add(ed);

        registry.put(eventClassName, declarations);
    }

    public static BlockInteraction createRecord(Event event)
    {
        // Look for this event type in the registry
        List<EventDeclaration> declarations = getInstance().registry.get(event.getClass().getSimpleName());

        if(declarations == null || declarations.size() == 0)
        {
            // Nothing declared for this event; abort
            return null;
        }

        BlockInteraction result = null;

        // Loop through the declarations, looking for a match
        for(EventDeclaration declaration : declarations)
        {
            if(declaration.shouldHandleEvent(event))
            {
                // Execute the constructor for the current declaration
                result = declaration.handleEvent(event);
                // Stop looping once we get something useful
                if(result != null)
                    break;
            }
        }

        return result;
    }
}
