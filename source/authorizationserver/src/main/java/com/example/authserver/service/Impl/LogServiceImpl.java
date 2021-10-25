package com.example.authserver.service.Impl;

import com.example.authserver.exception.CustomException;
import com.example.authserver.exception.ListenerExceptionObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class LogServiceImpl  {
    private Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    public void createListenerLogExceptionException(ListenerExceptionObject kafkaExceptionObject) throws CustomException {
        logger.info(kafkaExceptionObject.getStringObject()) ;
    }

    public String getIp() {
        String serverId;
        try {
            serverId = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            serverId = "127.0.0.1";
        }
        return serverId;
    }
}
