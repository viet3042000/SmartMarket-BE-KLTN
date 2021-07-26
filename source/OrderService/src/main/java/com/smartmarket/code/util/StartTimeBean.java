package com.smartmarket.code.util;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Date;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class StartTimeBean {

    public Long startTime = System.currentTimeMillis() ;

    public StartTimeBean(){
        this.startTime =  System.currentTimeMillis();
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }
}


//@Component
//@Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
//class StartTimeBean implements java.util.function.Supplier<Date> {
//
//    @Override
//    public Date get() {
//        return new Date();
//    }
//}