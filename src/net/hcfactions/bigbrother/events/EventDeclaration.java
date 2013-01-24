package net.hcfactions.bigbrother.events;

import net.hcfactions.bigbrother.model.BlockInteraction;
import org.bukkit.event.Event;

import java.lang.reflect.Constructor;

public class EventDeclaration {
    protected Class<? extends Event> eventType;
    protected Class<? extends BlockInteraction> model;

    public EventDeclaration(Class<? extends Event> eventType, Class<? extends BlockInteraction> model)
    {
        this.eventType = eventType;
        this.model = model;
    }

    public Class<? extends Event> getEventType()
    {
        return eventType;
    }

    public boolean shouldHandleEvent(Event event)
    {
        if(event.getClass() == eventType)
            return true;

        return false;
    }

    public BlockInteraction handleEvent(Event event)
    {
        BlockInteraction result = null;
        try
        {
            Constructor ctor = model.getConstructor(eventType);
            result = (BlockInteraction)(ctor.newInstance(event));
        }
        catch(Exception ex) { }

        return result;
    }
}