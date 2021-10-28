package com.smartmarket.code.service.impl;

import com.smartmarket.code.exception.CustomException;
import com.smartmarket.code.model.entitylog.ListenerExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceExceptionObject;
import com.smartmarket.code.model.entitylog.ServiceObject;
import com.smartmarket.code.model.entitylog.TargetObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class LogServiceImpl  {
    private Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    //  Target là lưu log mình giao dịch với BIC
    public void createTargetLog(TargetObject targetObject){
        logger.info(targetObject.getStringObject());
    }

    //  SOA là bảng lưu log giao dịch của client với hệ thống
    public void createSOALog2(ServiceObject serviceObject) throws CustomException {
        logger.info(serviceObject.getStringObject()) ;
    }
    //  SOA Exception là bảng lưu log giao dịch của client với hệ thống
    public void createSOALogException(ServiceExceptionObject serviceExceptionObject) throws CustomException {
        logger.info(serviceExceptionObject.getStringObject()) ;
    }

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
