package com.smartmarket.code.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
public class GetKeyPairUtil {
    public void getKeyPair(JSONObject jsonObject, Map<String, Object> keyPairs) throws JSONException {
        //Get key-pair in afterObj
//        JSONArray keys = jsonObject.names();
        Iterator<String> keys = jsonObject.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            Object o = jsonObject.get(key);

            // do something with jsonObject here
            Object value = o; // Here's your value
            keyPairs.put(key, o);
        }

//        for (int i = 0; i < keys.length(); i++) {
//            if( keys.get(i) instanceof String) {
//                String key = keys.getString(i); // Here's your key
//                String value = jsonObject.getString(key); // Here's your value
//                keyPairs.put(key, value);
//            }
//            else if (keys.get(i) instanceof Long){
//                String key = keys.getString(i); // Here's your key
//                Long value = jsonObject.getLong(key); // Here's your value
//                keyPairsLong.put(key, value);
//            }
//        }
    }
}
