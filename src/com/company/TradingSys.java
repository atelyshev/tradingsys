package com.company;

import com.company.components.*;
import com.company.core.AbstractEventDispatcher;
import com.company.core.Application;
import com.company.core.LazyLiveEventDispatcher;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.LazyStringArrayList;

import static com.company.components.PingComponent.*;

public class TradingSys<E, T extends GeneratedMessageV3.Builder<T>> extends Application
{
    public TradingSys(String confName, String instanceId) throws Exception
    {
        super(confName, instanceId);
        PingComponent.RegisterComponentFactories();
        PongComponent.RegisterComponentFactories();
        StartOfDayComponent.RegisterComponentFactories();
        LazyLiveEventDispatcher.RegisterComponentFactories();
    }

    public void Run() throws ClassNotFoundException
    {
        String cls = AbstractEventDispatcher.class.getCanonicalName();
        AbstractEventDispatcher ad = GetComponentManager().GetResource(cls);
        ad.Run();
    }
}
