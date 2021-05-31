package com.smartmarket.code.response;

public class DataCreateBIC {
    private String message ;
    private String createdate ;
    private final static String type = "CREATE";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public static String getType() {
        return type;
    }
}
