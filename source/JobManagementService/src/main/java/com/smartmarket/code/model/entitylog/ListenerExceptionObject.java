package com.smartmarket.code.model.entitylog;

import org.json.JSONObject;

public class ListenerExceptionObject {
//    String consumerName;
    String topicName;
    String tableName;
    String serviceName ; //update ...
    String messageTimestamp ;
    String errorMsg ;
    String errorCode ;
    String errorDetail;

    public ListenerExceptionObject(String topicName, String tableName,
                                   String serviceName, String messageTimestamp, String errorMsg,
                                   String errorCode, String errorDetail){
//        this.consumerName = consumerName;
        this.topicName = topicName;
        this.tableName = tableName;
        this.serviceName = serviceName;
        this.messageTimestamp = messageTimestamp;
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
        this.errorDetail = errorDetail;
    }

    public String getStringObject(){
        JSONObject json = new JSONObject();
//        json.put("consumerName",consumerName);
        json.put("topicName",topicName);
        json.put("tableName",tableName);
        json.put("serviceName",serviceName);
        json.put("messageTimestamp",messageTimestamp);
        json.put("errorMsg",errorMsg);
        json.put("errorCode",errorCode);
        json.put("errorDetail",errorDetail);
        return json.toString();
    }
}
