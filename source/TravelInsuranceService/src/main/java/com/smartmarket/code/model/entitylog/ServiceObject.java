package com.smartmarket.code.model.entitylog;

import lombok.AllArgsConstructor;
import org.json.JSONObject;

@AllArgsConstructor
public class ServiceObject {

    String logName;
    String requestId;
    String requestTime;
    String transactionId;
    String sourceId;
    String targetId;
    String messageTimestamp;
    String serviceName;
    String serviceVersion;
    String timeDuration;
    String logType;
    org.json.JSONObject transactionDetail;
    String responseStatus;//httpcode
    String errorCode;
    String errorMsg;
    String logTimestamp;
    String hostName;
    String clientIp;

    public String getStringObject() {
        JSONObject json = new JSONObject();
        json.put("logName", logName);
        json.put("requestId", requestId);
        json.put("requestTime", requestTime);
        json.put("transactionId", transactionId);
        json.put("sourceId", sourceId);
        json.put("targetId", targetId);
        json.put("messageTimestamp", messageTimestamp);
        json.put("serviceName", serviceName);
        json.put("serviceVersion", serviceVersion);
        json.put("timeDuration", timeDuration);
        json.put("logType", logType);
        json.put("transactionDetail", transactionDetail);
        json.put("responseStatus", responseStatus);
        json.put("errorCode", errorCode);
        json.put("errorMsg", errorMsg);
        json.put("logTimestamp", logTimestamp);
        json.put("hostName", hostName);
        json.put("clientIp", clientIp);

        return json.toString();
    }
}
