package com.company;

import dto.proto.Tradingsys;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            TradingSys ts = new TradingSys("file://D:/dev/TradingSys/conf/tradingsys.cfg", "test");
            ts.Init(Tradingsys.TradingSysConfig.newBuilder());
            ts.InitComponents();
            // Blocking call
            ts.Run();
            ts.Shutdown();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
