package com.company.core;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.sun.jmx.snmp.Timestamp;
import dto.proto.Config;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Application<E, T extends GeneratedMessageV3.Builder<T>>
{
    public Application(String confName, String instanceId) throws Exception
    {
        _confName = confName;
        _instanceId = instanceId;
    }

    public void Init(GeneratedMessageV3.Builder<T> builder) throws Exception
    {
        ConfigParser cp = new ConfigParser();
        _config = cp.Parse(_confName, builder);
        String className = _config .getClass().getName();
        Method method = Class.forName(className).getMethod("getApplicationConfig");
        Config.ApplicationConfig appConf = (Config.ApplicationConfig)method.invoke(_config);
        _instanceId = appConf.getInstanceId().toLowerCase();
        String appName =  appConf.getApplicationName().toLowerCase();

        method = Class.forName(className).getMethod("getOutputDir");
        _outputDir = (String) method.invoke(_config);

        if (_outputDir.isEmpty())
        {
            Timestamp ts = new Timestamp(new Date().getTime());
            String ds = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            _outputDir = String.format("%s/log/output/%s/%s", System.getenv("HOME"), ds, appName);
            Path pathToFile = Paths.get(_outputDir);
            Files.createDirectories(pathToFile);
            System.setProperty("user.dir", _outputDir);
        }

        cp.SaveParsed(String.format("%s/parsed.cfg",_outputDir));
        cp.SaveOriginal(String.format("%s/original.cfg",_outputDir));
        method = Class.forName(className).getMethod("getComponentsConfig");
        Config.ComponentsConfig cf = (Config.ComponentsConfig)method.invoke(_config);
        _componentManager = new ComponentManager();
        _componentManager.Init(cf);
    }

    public void InitComponents()
    {
        _componentManager.InitComponents((Message)_config);
    }

    public ComponentManager GetComponentManager()
    {
        return _componentManager;
    }

    public void Shutdown()
    {
        _componentManager.ShutdownComponents();
    }

    private E _config;
    private String _confName;
    private String _outputDir;
    private ComponentManager _componentManager;
    private String _instanceId;
}
