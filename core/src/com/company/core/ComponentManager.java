package com.company.core;

import dto.proto.Config;
import dto.proto.Config.ComponentsConfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import com.google.protobuf.*;
import java.util.function.Function;

public class ComponentManager
{
    public boolean Init(ComponentsConfig config) throws Exception
    {
        _config = config;

        for (Config.Component c : _config.getComponentsList())
        {
            String cn = c.getClassName();
            Function creator = _factories.get(cn);
            if(creator != null)
            {
                _componentsMap.put(cn, (AbstractComponent)creator.apply(this));
            }
        }
        return true;
    }

    public static <T> void RegisterComponentFactory(String cName)
    {
        Function<ComponentManager, AbstractComponent> cr = (parm)->
        {
            try
            {
                Class<?> cl = Class.forName(cName);
                Constructor<?> cons = cl.getConstructor(ComponentManager.class);
                return (AbstractComponent)cons.newInstance(parm);
            }
            catch(NoSuchMethodException ex){
                ex.printStackTrace();
            }
            catch(ClassNotFoundException ex){
                ex.printStackTrace();
            }
            catch(InstantiationException ex){
                ex.printStackTrace();
            }
            catch(IllegalAccessException ex){
                ex.printStackTrace();
            }
            catch(InvocationTargetException ex){
                ex.printStackTrace();
            }
            return null;
        };
        _factories.put(cName, cr);
    }

    public <T> T GetResource(String cName) throws ClassNotFoundException
    {
        T res;
        if ((res = (T)_componentsMap.get(cName)) != null)
        {
            return res;
        }

        Class<?> cl = Class.forName(cName);
        for(AbstractComponent v : _componentsMap.values())
        {
            if (cl.isInstance(v))
            {
                res = (T) v;
                break;
            }
        }
        return res;
    }

    public void InitComponents(Message config)
    {
        ConfigAccessor ca = new ConfigAccessor(config);
        for(AbstractComponent v : _componentsMap.values())
        {
            v.Init(ca);
        }
    }

    public void ShutdownComponents(){}
    private ComponentsConfig _config;
    private static HashMap<String, Function> _factories = new HashMap<String, Function>();
    private HashMap<String, AbstractComponent> _componentsMap = new HashMap<String, AbstractComponent>();
}
