package com.smartmarket.code.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

@Service
public class GetKeyPairUtil {
    public void getKeyPair(JSONObject jsonObject, Map<String, Object> keyPairs) throws JSONException {
        //Get key-pair in afterObj
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            Object o = jsonObject.get(key);
            if(!JSONObject.NULL.equals(o)) {
                keyPairs.put(key,o);
            }
        }
    }
}
