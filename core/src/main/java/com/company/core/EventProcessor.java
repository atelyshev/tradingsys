package com.company.core;

import java.util.HashMap;
import java.util.HashSet;

public class EventProcessor
{
    /**
     * Broadcast event to all subscribers
     * @param event
     */
    public void ProcessEvent(EventBase event)
    {
        HashSet<IEventHandler> handlers = _eventHandlers.get(event.GetName());
        if (handlers != null)
        {
            for (IEventHandler eh : handlers)
            {
                eh.HandleEvent(event.GetMessage());
            }
        }
        else
        {
            System.out.println("Event handler for event " + event.GetName() + " not found");
        }
    }

    public void LogStats()
    {

    }

    public <T> void RegisterHandler(String typeId, IEventHandler<T> handler)
    {
        HashSet<IEventHandler> handlers = null;

        if (!_eventHandlers.containsKey(typeId))
        {
            handlers = new HashSet<IEventHandler>();
            _eventHandlers.put(typeId, handlers);
        }else
        {
            handlers = _eventHandlers.get(typeId);
        }
        handlers.add(handler);
    }

    private HashMap<String, HashSet<IEventHandler>> _eventHandlers = new HashMap<String, HashSet<IEventHandler>>();
    private long _eventCount = 0;
}
