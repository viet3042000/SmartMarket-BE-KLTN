package com.smartmarket.code.model.entitylog;

import lombok.AllArgsConstructor;
import net.minidev.json.JSONObject;

@AllArgsConstructor
public class TargetObject {
    String logName;
    String transactionId;
    String messageId;
    String targetService;
    String logType;
    String transactionDetail;
    String logTimestamp;
    String messageTimestamp;
    String timeDuration;

    public String getStringObject() {
        JSONObject json = new JSONObject();
        json.put("logName", logName);
        json.put("transactionId", transactionId);
        json.put("messageId", messageId);
        json.put("targetService", targetService);
        json.put("logType", logType);
        json.put("transactionDetail", transactionDetail);
        json.put("logTimestamp", logTimestamp);
        json.put("messageTimestamp", messageTimestamp);
        json.put("timeDuration", timeDuration);

        return json.toString();
    }
}
