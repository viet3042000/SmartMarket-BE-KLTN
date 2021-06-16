package com.smartmarket.code.service.impl;

import com.smartmarket.code.exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Service
public class LogServiceImpl  {
    private Logger logger = LoggerFactory.getLogger(LogServiceImpl.class);

    //  Target là lưu log mình giao dịch với BIC
    public void createTargetLog(String logBody){
        logger.info(logBody);
    }

    //  SOA là bảng lưu log giao dịch của client với hệ thống
    public void createSOALog2(String logBody) throws CustomException {
        logger.info(logBody) ;
    }
    //  SOA Exception là bảng lưu log giao dịch của client với hệ thống
    public void createSOALogException(String logBody) throws CustomException {
        logger.info(logBody) ;
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
