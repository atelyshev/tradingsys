package com.company.components;

import com.company.core.*;
import dto.proto.Config;
import dto.proto.Messages;

import java.sql.Timestamp;

public class PongComponent extends AbstractComponent
{
    public PongComponent(ComponentManager cm) throws ClassNotFoundException
    {
        _ed = cm.GetResource(AbstractEventDispatcher.class.getName());
        String cls = Messages.PongMessage.class.getCanonicalName();
        _ed.RegisterHandler(cls, new PongMessageEventHandler());
        cls = Messages.StartOfDayMessage.class.getCanonicalName();
        _ed.RegisterHandler(cls, new StartOfDayEventHandler());
    }

    public boolean Init(ConfigAccessor ca)
    {
        _pongConf = ca.GetConfig("dto.proto.PongConfig");
        return true;
    }

    public static void RegisterComponentFactories() throws InstantiationException, IllegalAccessException
    {
        ComponentManager.RegisterComponentFactory(new Exception().getStackTrace()[0].getClassName());
    }

    class StartOfDayEventHandler implements IEventHandler<Messages.StartOfDayMessage>
    {
        @Override
        public void HandleEvent(Messages.StartOfDayMessage event)
        {
            System.out.println("Start of day!!!");
        }
    }

    class PongMessageEventHandler implements IEventHandler<Messages.PongMessage>
    {
        @Override
        public void HandleEvent(Messages.PongMessage event)
        {
            System.out.println("Received " + event.getSendText());
            Messages.PingMessage.Builder pmb = Messages.PingMessage.newBuilder();
            pmb.setSendText("Pong");
            Messages.PingMessage pm = pmb.build();
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            _ed.PublishRealTimeEvent(new EventBase(ts, pm));
            System.out.println("Send " + pmb.getSendText());
        }
    }

    private Config.ApplicationConfig _appConf;
    private Config.PongConfig _pongConf;
    private AbstractEventDispatcher _ed;
}
