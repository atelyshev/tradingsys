package com.company.core;

import java.util.ArrayList;

public abstract class AbstractConfigAccessor
{
    public String GetNextLine()
    {
        if (_currentLine >= _lines.size())
            return null;
        return _lines.get(_currentLine++);
    }

    public abstract void Open(String path) throws Exception;
    public String GetCfgLocation()
    {
        return _cgfLocation;
    }

    protected int _currentLine = 0;
    protected ArrayList<String> _lines = new ArrayList<>();
    protected String _cgfLocation;
}
