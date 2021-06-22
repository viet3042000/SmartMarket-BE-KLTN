package com.smartmarket.code.model.entitylog;

import lombok.AllArgsConstructor;
import org.json.JSONObject;

@AllArgsConstructor
public class TargetObject {
    String logName;
    String transactionId;
    String requestId;
    String requestTime;
    String targetService;
    String logType;
    JSONObject transactionDetail;
    String logTimestamp;
    String messageTimestamp;
    String timeDuration;

    public String getStringObject() {
        JSONObject json = new JSONObject();
        json.put("logName", logName);
        json.put("transactionId", transactionId);
        json.put("requestId", requestId);
        json.put("requestTime", requestTime);
        json.put("targetService", targetService);
        json.put("logType", logType);
        json.put("transactionDetail", transactionDetail);
        json.put("logTimestamp", logTimestamp);
        json.put("messageTimestamp", messageTimestamp);
        json.put("timeDuration", timeDuration);

        return json.toString();
    }

}
