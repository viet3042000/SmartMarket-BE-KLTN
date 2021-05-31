package com.smartmarket.code.constants;

import java.lang.reflect.Field;

public class ResponseCode {


    private static String[] messageStrings;
    private static final ResponseCode instance = new ResponseCode();

    private ResponseCode() {
        messageStrings = new String[2000];
        Field[] fields = ResponseCode.class.getFields();
        for (Field f : fields) {
            try {
                f.setAccessible(true);
                String temp = f.getName().replace('_', ' ').toLowerCase();
                messageStrings[f.getInt(instance)] = temp;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String getMessage(int code) {
        return messageStrings[code];
    }


    public static final int SUCCESS = 0;
    public static final int UNKNOWN_ERROR = 1;
    public static final int WRONG_DATA_FORMAT = 2;
    public static final int INVALID_TOKEN = 3;
    public static final int TOKEN_EXPIRED = 4;
    public static final int DUPLICATE_DATA = 5;
    public static final int ERROR_DUPLICATE_LOT_NUMBER = 6;
}
