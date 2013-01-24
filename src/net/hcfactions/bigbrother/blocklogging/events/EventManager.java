package net.hcfactions.bigbrother.blocklogging.events;

import net.hcfactions.bigbrother.blocklogging.model.*;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.material.EnderChest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is responsible for taking an incoming event and returning a Model describing it, which we can then save to the database
 * It maintains a HashMap where each key is the Event name, and value is a list of potential EventDeclarations that might used
 */
public class EventManager {
    private static HashMap<String, ArrayList<EventDeclaration>> registry = new HashMap<String, ArrayList<EventDeclaration>>();

    /**
     * Registers a pre-determined list of EventDeclarations
     */
    static
    {
        register(new BlockEventDeclaration(BlockPlaceEvent.class, ChestBlockChange.class, Material.CHEST, Material.ENDER_CHEST));
        register(new BlockEventDeclaration(BlockPlaceEvent.class, SignBlockChange.class, Material.SIGN_POST, Material.WALL_SIGN));

        register(new BlockEventDeclaration(BlockBreakEvent.class, ChestBlockChange.class, Material.CHEST, Material.ENDER_CHEST));
        register(new BlockEventDeclaration(BlockBreakEvent.class, SignBlockChange.class, Material.SIGN_POST, Material.WALL_SIGN));

        register(new EventDeclaration(SignChangeEvent.class, SignBlockChange.class));

        register(new InventoryEventDeclaration(InventoryOpenEvent.class, ChestInventoryOpen.class, InventoryType.CHEST));

        // Keep tabs on the breaking of any ore blocks
        register(new OreMinedEventDeclaration(BlockBreakEvent.class, OreBreak.class, Material.DIAMOND_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE, Material.COAL_ORE, Material.REDSTONE_ORE, Material.GLOWING_REDSTONE_ORE));
        // Keep tabs on players removing iron and gold from furnaces
        register(new InventoryResultRemovalEventDeclaration(InventoryClickEvent.class, PlayerItemStack.class, InventoryType.FURNACE, Material.IRON_INGOT, Material.GOLD_INGOT));
    }

    /**
     * Adds the EventDeclataion to the EventManager's internal HashMap
     * @param ed The declaration to register
     */
    private static void register(EventDeclaration ed)
    {
        String eventClassName = ed.getEventType().getSimpleName();
        ArrayList<EventDeclaration> declarations = new ArrayList<EventDeclaration>();

        if(registry.containsKey(eventClassName))
            declarations = registry.get(eventClassName);

        declarations.add(ed);

        registry.put(eventClassName, declarations);
    }

    /**
     * Take a server Event and return a Model, or null if no registered declaration can handle the Event
     * It loops through the EventDeclarations for the given event, determines which (if any) can handle it, and returns a new Model or null
     * @param event The Event which was fired
     * @return
     */
    public static BaseModel createRecord(Event event)
    {
        // Look for this event type in the registry
        List<EventDeclaration> declarations = registry.get(event.getClass().getSimpleName());

        if(declarations == null || declarations.size() == 0)
        {
            // Nothing declared for this event; abort
            return null;
        }

        BaseModel result = null;

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
