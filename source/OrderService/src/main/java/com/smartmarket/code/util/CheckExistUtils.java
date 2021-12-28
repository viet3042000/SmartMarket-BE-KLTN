package com.smartmarket.code.util;

import org.json.JSONObject;

public class CheckExistUtils {
    public static boolean hasValue(JSONObject json , String key) {
        if(json.isNull(key)){
            return false;
        }
        return true;
    }
}
