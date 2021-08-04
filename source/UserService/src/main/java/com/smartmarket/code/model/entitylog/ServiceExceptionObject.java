package com.smartmarket.code.model.entitylog;

import lombok.AllArgsConstructor;
import org.json.JSONObject;

//@AllArgsConstructor
public class ServiceExceptionObject {
    String logName;
    String logType;
    String requestId ;
    String requestTime;
    String transactionId;
    String messageTimestamp ;
    String serviceName ;
    String operationName ;
    String serviceVersion ;
    String hostName ;
    String errorMsg ;//result msg
    String errorCode ;//result code
    String errorDetail ;// exception chi tiết
    String clientIp ;

    public ServiceExceptionObject(String logName,String logType,String requestId ,String requestTime,
                                  String transactionId, String messageTimestamp ,String serviceName ,
                                  String operationName , String serviceVersion , String hostName ,
                                  String errorMsg , String errorCode ,String errorDetail, String clientIp){

        this.logName = logName;
        this.logType = logType;
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.transactionId = transactionId;
        this.messageTimestamp = messageTimestamp;
        this.serviceName = serviceName;
        this.operationName = operationName;
        this.serviceVersion = serviceVersion;
        this.hostName = hostName;
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
        this.clientIp = clientIp;

    }

    public String getStringObject(){
        JSONObject json = new JSONObject();
        json.put("logName",logName);
        json.put("logType",logType);
        json.put("requestId",requestId);
        json.put("requestTime",requestTime);
        json.put("transactionId",transactionId);
        json.put("messageTimestamp",messageTimestamp);
        json.put("serviceName",serviceName);
        json.put("operationName",operationName);
        json.put("serviceVersion",serviceVersion);
        json.put("errorCode",errorCode);
        json.put("errorMsg",errorMsg);
        json.put("errorDetail",errorDetail);
        json.put("hostName",hostName);
        json.put("clientIp",clientIp);
        return json.toString();
    }
}
