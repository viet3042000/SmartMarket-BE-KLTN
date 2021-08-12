package com.smartmarket.code.model.entitylog;

import org.json.JSONObject;

public class JobManagementExceptionObject {
    String logName;
    String serviceName ;
    String messageTimestamp ;
    String errorMsg ;
    String errorCode ;
    String errorDetail;

    public JobManagementExceptionObject(String logName,String serviceName,String messageTimestamp, String errorMsg,
                                   String errorCode, String errorDetail){
        this.logName = logName;
        this.serviceName = serviceName;
        this.messageTimestamp = messageTimestamp;
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
    }

    public String getStringObject(){
        JSONObject json = new JSONObject();
        json.put("logName",logName);
        json.put("serviceName",serviceName);
        json.put("messageTimestamp",messageTimestamp);
        json.put("errorMsg",errorMsg);
        json.put("errorCode",errorCode);
        json.put("errorDetail",errorDetail);
        return json.toString();
    }
}
