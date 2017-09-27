package com.company.core;

import com.google.protobuf.*;
import com.google.protobuf.Descriptors.*;

import java.util.List;

public class ConfigAccessor
{
    public ConfigAccessor(Message config)
    {
        _globalConfig = config;
    }

    public <T extends Message> T GetGlobalConfig()
    {
        return (T)_globalConfig;
    }

    public <T extends Message> T GetConfig(String findFieldName)
    {
        Descriptor d = _globalConfig.getDescriptorForType();
        List<FieldDescriptor> lst = d.getFields();
        for (FieldDescriptor fd : lst)
        {
            try{
                Descriptor d2 = fd.getMessageType();
                String str = d2.getFullName();
                if (str.equals(findFieldName))
                {
                    return (T)_globalConfig.getField(fd);
                }
            }
            catch (UnsupportedOperationException ex) // It is ok to have this exception, not any fields allow getMessageType()
            {
                System.out.println("Exception in ConfigAccessor. It is expected. Ignore it.");
            }
        }
        return null;
    }

    private Message _globalConfig;
}
