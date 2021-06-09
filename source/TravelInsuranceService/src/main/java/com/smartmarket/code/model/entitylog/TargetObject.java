package com.smartmarket.code.model.entitylog;

import lombok.AllArgsConstructor;
import net.minidev.json.JSONObject;

@AllArgsConstructor
public class TargetObject {
    String targetLog;
    String transactionId;
    String messageId;
    String targetService;
    String status;
    String transactionDetail;
    String logTimestamp;
    String messageTimestamp;
    String timeDuration;

    public String getStringObject() {
        JSONObject json = new JSONObject();
        json.put("targetLog", targetLog);
        json.put("transactionId", transactionId);
        json.put("messageId", messageId);
        json.put("targetService", targetService);
        json.put("status", status);
        json.put("transactionDetail", transactionDetail);
        json.put("logTimestamp", logTimestamp);
        json.put("messageTimestamp", messageTimestamp);
        json.put("timeDuration", timeDuration);

        return json.toString();
    }
}
