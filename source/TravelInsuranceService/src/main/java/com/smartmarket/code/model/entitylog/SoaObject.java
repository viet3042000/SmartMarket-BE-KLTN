package com.smartmarket.code.model.entitylog;

import lombok.AllArgsConstructor;
import org.json.JSONObject;

@AllArgsConstructor
public class SoaObject {
    String MESSAGEID ; String TRANSACTIONID; String SOURCEID;
    String TARGETID; String MESSAGETIMESTAMP;String SERVICENAME;
    String SERVICEVERSION; String TIMEDURATION; String STATUS;
    String TRANSACTIONDETAIL; String RESPONSESTATUS; String ERRORCODE;
    String ERRORMSG; String LOGTIMESTAMP; String HOSTNAME; String CLIENTIP;

    public String getStringObject(){
        JSONObject json = new JSONObject();
        json.put("MESSAGEID",MESSAGEID);
        json.put("TRANSACTIONID",TRANSACTIONID);
        json.put("SOURCEID",SOURCEID);
        json.put("TARGETID",TARGETID);
        json.put("MESSAGETIMESTAMP",MESSAGETIMESTAMP);
        json.put("SERVICENAME",SERVICENAME);
        json.put("SERVICEVERSION",SERVICEVERSION);
        json.put("TIMEDURATION",TIMEDURATION);
        json.put("STATUS",STATUS);
        json.put("TRANSACTIONDETAIL",TRANSACTIONDETAIL);
        json.put("RESPONSESTATUS",RESPONSESTATUS);
        json.put("ERRORCODE",ERRORCODE);
        json.put("ERRORMSG",ERRORMSG);
        json.put("LOGTIMESTAMP",LOGTIMESTAMP);
        json.put("HOSTNAME",HOSTNAME);
        json.put("CLIENTIP",CLIENTIP);

        return json.toString();
    }
}
