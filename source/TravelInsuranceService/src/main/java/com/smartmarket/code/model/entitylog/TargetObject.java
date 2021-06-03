package com.smartmarket.code.model.entitylog;

import lombok.AllArgsConstructor;
import net.minidev.json.JSONObject;

@AllArgsConstructor
public class TargetObject {
    String TRANSACTIONID; String MESSAGEID; String TARGETSERVICE;
    String MESSAGETYPE; String TRANSACTIONDETAIL; String LOGTIMESTAMP ;
    String MESSAGETIMESTAMP; String TIMEDURATION;

    public String getStringObject() {
        JSONObject json = new JSONObject();
        json.put("TRANSACTIONID",TRANSACTIONID);
        json.put("MESSAGEID",MESSAGEID);
        json.put("TARGETSERVICE",TARGETSERVICE);
        json.put("MESSAGETYPE",MESSAGETYPE);
        json.put("TRANSACTIONDETAIL",TRANSACTIONDETAIL);
        json.put("LOGTIMESTAMP",LOGTIMESTAMP);
        json.put("MESSAGETIMESTAMP",MESSAGETIMESTAMP);
        json.put("TIMEDURATION",TIMEDURATION);

        return json.toString();
    }
}
