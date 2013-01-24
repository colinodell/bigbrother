package net.hcfactions.bigbrother.blocklogging.events;

import net.hcfactions.bigbrother.blocklogging.model.BaseModel;
import net.hcfactions.bigbrother.blocklogging.model.BlockInteraction;
import org.bukkit.event.Event;

import java.lang.reflect.Constructor;

/**
 * Defines a single relationship between a server event and a model that describes the event
 * In plain English: when the given Event occurs, I want this Model to be created based on the event data
 */
public class EventDeclaration {
    protected Class<? extends Event> eventType;
    protected Class<? extends BaseModel> model;

    /**
     * Defines a new declaration that, when the given event occurs, a new BlockInteraction model instance should be created based on that event data
     * @param eventType
     * @param model
     */
    public EventDeclaration(Class<? extends Event> eventType, Class<? extends BaseModel> model)
    {
        this.eventType = eventType;
        this.model = model;
    }

    /**
     * Returns the configured event type this declaration responds to
     * @return
     */
    public Class<? extends Event> getEventType()
    {
        return eventType;
    }

    /**
     * Determine if this declaration can/should handle the given event
     * @param event The current event to check
     * @return
     */
    public boolean shouldHandleEvent(Event event)
    {
        if(event.getClass() == eventType)
            return true;

        return false;
    }

    /**
     * Returns a new instance of the model based on the given event
     * @param event The current event being executed; passed into the model's constructor
     * @return Model instance based on the given event
     */
    public BaseModel handleEvent(Event event)
    {
        BaseModel result = null;
        try
        {
            Constructor ctor = model.getConstructor(eventType);
            result = (BaseModel)(ctor.newInstance(event));
        }
        catch(Exception ex) { }

        return result;
    }
}