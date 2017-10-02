package com.company.core;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LazyLiveEventDispatcher extends AbstractEventDispatcher
{
    public LazyLiveEventDispatcher(ComponentManager cm)
    {
        _eventScheduler = new FutureEventScheduler();
    }

    public static void RegisterComponentFactories() throws InstantiationException, IllegalAccessException
    {
        ComponentManager.RegisterComponentFactory(new Exception().getStackTrace()[0].getClassName());
    }

    @Override
    public void PublishRealTimeEvent(EventBase event)
    {
        _queueCount.incrementAndGet();
        while(!_isShutdown.get() && !_queue.add(event))
        {
            ;
        }

        _lock.lock();
        try
        {
            _cv.signal();
        }
        finally
        {
            _lock.unlock();
        }
    }

    @Override
    public void ScheduleFutureEvent(EventBase event, boolean ignoreCheck)
    {
        if (_isShutdown.get())
            return;
        _eventScheduler.ScheduleEvent(event);
    }

    @Override
    public void Run()
    {
        Runnable task = () -> { _eventScheduler.Run(); };
        _thr = new Thread(task);
        _thr.start();

        while (!_isShutdown.get())
        {
            EventBase event = null;
            while ((event = _queue.poll()) != null)
            {
                if (_eventCount.incrementAndGet() % 100 == 0)
                {
                    System.out.println(_eventCount.get());
                }
                _queueCount.decrementAndGet();
                _eventProcessor.ProcessEvent(event);
            }

            _lock.lock();
            try
            {
                if (_queue.isEmpty())
                {
                    _cv.await(100, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } finally
            {
                _lock.unlock();
            }
        }
    }

    @Override
    public int QueueCount()
    {
        return _queueCount.get();
    }

    @Override
    public long ProcessedEvents()
    {
        return _eventCount.get();
    }

    final Lock _lock = new ReentrantLock();
    final Condition _cv = _lock.newCondition();
    private AtomicBoolean _isShutdown = new AtomicBoolean(false);
    private AtomicInteger _queueCount = new AtomicInteger(0);
    private AtomicLong _eventCount = new AtomicLong(0);
    private ConcurrentLinkedQueue<EventBase> _queue = new ConcurrentLinkedQueue<EventBase>();
    private Thread _thr;
    private FutureEventScheduler _eventScheduler;
}
