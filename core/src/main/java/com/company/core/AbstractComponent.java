package com.company.core;

public abstract class AbstractComponent
{
    public boolean Init(ConfigAccessor ca)
    {
        return true;
    }

    public void Shutdown() { }

    public void SetName(String name)
    {
        _name = name;
    }

    public String GetName()
    {
        return _name;
    }

    private String _name;
}
