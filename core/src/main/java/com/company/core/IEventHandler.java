package com.company.core;

public interface IEventHandler<T>
{
    public void HandleEvent(T event);
}
