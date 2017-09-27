package com.company.core;

public class ConfigAccessorFactory
{
    public static AbstractConfigAccessor CreateConfigAccessor(String path) throws Exception
    {
        if (path.toLowerCase().contains("zoo://"))
        {
            return new ZooKeeperConfigAccessor(path.substring("zoo://".length()));
        }
        else if (path.toLowerCase().contains("file://"))
        {
            return new FileSystemConfigAccessor(path.substring("file://".length()));
        }
        else
        {
            throw new Exception("Unknown configuration URI schema. use zoo:// for Zookeeper or file:// for local file.");
        }
    }
}
