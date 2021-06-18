package com.smartmarket.code.util;

public class CheckFormatUtils {

    public static boolean checkFormat(EJson jsonObjectReponseCreate){
        boolean isSucceededNull = jsonObjectReponseCreate.hasValue("succeeded");
        boolean isDataNull = jsonObjectReponseCreate.hasValue("data");

        if(isSucceededNull==false && isDataNull==false){
            return true;
        }else {
            return false;
        }
    }
}
