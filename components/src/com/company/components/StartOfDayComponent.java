package com.company.components;

import com.company.core.*;
import dto.proto.Config;
import dto.proto.Messages;

import java.sql.Timestamp;
import java.util.*;

public class StartOfDayComponent extends AbstractComponent
{
    public StartOfDayComponent (ComponentManager cm) throws ClassNotFoundException
    {
        _ed = cm.GetResource(AbstractEventDispatcher.class.getName());
        String cls = Messages.PongMessage.class.getCanonicalName();
    }

    public boolean Init(ConfigAccessor ca)
    {
        _sodConf = ca.GetConfig("dto.proto.StartEndOfDayConfig");
        String [] parts = _sodConf.getSodTime().split(":");
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        Date d = c.getTime();
        new Timer().schedule(wrap(()->
        {
            Messages.StartOfDayMessage.Builder sodb = Messages.StartOfDayMessage.newBuilder();
            Messages.StartOfDayMessage sod = sodb.build();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            _ed.PublishRealTimeEvent(new EventBase(ts, sod));
        }), d);
        return true;
    }

    private static TimerTask wrap(Runnable r)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                r.run();
            }
        };
    }

    public static void RegisterComponentFactories() throws InstantiationException, IllegalAccessException
    {
        ComponentManager.RegisterComponentFactory(new Exception().getStackTrace()[0].getClassName());
    }

    private Config.ApplicationConfig _appConf;
    private Config.StartEndOfDayConfig _sodConf;
    private AbstractEventDispatcher _ed;
}
