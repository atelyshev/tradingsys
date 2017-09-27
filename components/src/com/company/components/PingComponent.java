package com.company.components;

import com.company.core.*;
import dto.proto.Config;
import dto.proto.Messages;

import java.sql.Timestamp;

public class PingComponent extends AbstractComponent
{
    public PingComponent(ComponentManager cm) throws ClassNotFoundException
    {
        _ed = cm.GetResource(AbstractEventDispatcher.class.getName());
        String cls = Messages.PingMessage.class.getCanonicalName();
        _ed.RegisterHandler(cls, new PingMessageEventHandler());
        cls = Messages.StartOfDayMessage.class.getCanonicalName();
        _ed.RegisterHandler(cls, new PingComponent.StartOfDayEventHandler());
    }

    public boolean Init(ConfigAccessor ca)
    {
        _pingConf = ca.GetConfig("dto.proto.PingConfig");
        Run();
        return true;
    }

    public static void RegisterComponentFactories() throws InstantiationException, IllegalAccessException
    {
        ComponentManager.RegisterComponentFactory(new Exception().getStackTrace()[0].getClassName());
    }

    private void Run()
    {
        Messages.PongMessage.Builder pmb = Messages.PongMessage.newBuilder();
        pmb.setSendText("Ping");
        Messages.PongMessage pm = pmb.build();
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        _ed.PublishRealTimeEvent(new EventBase(ts, pm));
        System.out.println("Sent " + pmb.getSendText());
    }

    class StartOfDayEventHandler implements IEventHandler<Messages.StartOfDayMessage>
    {
        @Override
        public void HandleEvent(Messages.StartOfDayMessage event)
        {
            System.out.println("Start of day!!!");
        }
    }

    class PingMessageEventHandler implements IEventHandler<Messages.PingMessage>
    {
        @Override
        public void HandleEvent(Messages.PingMessage event)
        {
            System.out.println("Received " + event.getSendText());
            Messages.PongMessage.Builder pmb = Messages.PongMessage.newBuilder();
            pmb.setSendText("Ping");
            Messages.PongMessage pm = pmb.build();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            _ed.PublishRealTimeEvent(new EventBase(ts, pm));
            System.out.println("Sent " + pmb.getSendText());
        }
    }

    private Config.ApplicationConfig _appConf;
    private Config.PingConfig _pingConf;
    private AbstractEventDispatcher _ed;
}
