package com.company.core;


public abstract class AbstractEventDispatcher extends AbstractComponent
{
    public abstract void PublishRealTimeEvent(EventBase event);
    public abstract void ScheduleFutureEvent(EventBase event, boolean ignoreCheck);
    public <T> void RegisterHandler(String typeId, IEventHandler<T> handler)
    {
        _eventProcessor.RegisterHandler(typeId, handler);
    }

    public abstract void Run();
    public abstract int QueueCount();
    public abstract long ProcessedEvents();


    protected EventProcessor _eventProcessor = new EventProcessor();
}
