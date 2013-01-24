package net.hcfactions.bigbrother.blocklogging.events;

import net.hcfactions.bigbrother.blocklogging.model.BaseModel;
import net.hcfactions.bigbrother.blocklogging.model.BlockInteraction;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An EventDeclaration that also filters based on the block type
 */
public class BlockEventDeclaration extends EventDeclaration {
    protected List<Material> supportedMaterials = new ArrayList<Material>();

    public BlockEventDeclaration(Class<? extends Event> eventType, Class<? extends BaseModel> model, Material material)
    {
        super(eventType, model);
        this.supportedMaterials = new ArrayList<Material>();
        this.supportedMaterials.add(material);
    }

    public BlockEventDeclaration(Class<? extends Event> eventType, Class<? extends BaseModel> model, Material... materials)
    {
        super(eventType, model);
        this.supportedMaterials = new ArrayList<Material>(Arrays.asList(materials));
    }

    public boolean shouldHandleEvent(Event e)
    {
        if(!super.shouldHandleEvent(e))
            return false;

        // If no materials are listed, assume this applies to any/all
        if(this.supportedMaterials.size() == 0)
            return true;

        BlockEvent event = (BlockEvent)e;

        // Check if this material is supported
        if(this.supportedMaterials.contains(event.getBlock().getType()))
            return true;

        // We don't handle this type of block
        return false;
    }
}
