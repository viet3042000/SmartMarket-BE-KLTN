package com.smartmarket.code.util;

public class CheckFormatUtils {

    public static boolean checkFormat(EJson jsonObjectReponseCreate){
        boolean isSucceededNull = jsonObjectReponseCreate.hasValue("succeeded");
        boolean isDataNull = jsonObjectReponseCreate.hasValue("data");

        if(isSucceededNull==true && isDataNull==true){
            return true;
        }else {
            return false;
        }
    }

}
