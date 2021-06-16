package com.smartmarket.code.listener;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

public class CacheEventLogger implements CacheEventListener<Object, Object> {
    @Override
    public void onEvent(CacheEvent<?, ?> cacheEvent) {
        System.out.println("log" + cacheEvent.getKey() + cacheEvent.getOldValue() + cacheEvent.getNewValue());
    }

//    @Override
//    public void onEvent(CacheEvent cacheEvent) {
//        System.out.println("log" + cacheEvent.getKey() + cacheEvent.getOldValue()+ cacheEvent.getNewValue());
//    }
}


