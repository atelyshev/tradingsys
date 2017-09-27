package com.company.core;

import com.google.protobuf.*;

import java.sql.Timestamp;

public class EventBase
{
    public EventBase(Timestamp ts, Message protoMsg)
    {
        _ts = ts;
        _protoMsg = protoMsg;
        _typeId = _protoMsg.getClass().getCanonicalName();
    }

    public Timestamp GetTimestamp()
    {
        return _ts;
    }

    public String GetName(){return _typeId;}
    public Message GetMessage(){return _protoMsg;}

    private Timestamp _ts;
    private Message _protoMsg;
    private String _typeId;
}
