package com.smartmarket.code.request;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class BaseRequest implements Serializable {

//    @NotNull(message = "abc")
    private String requestId ;
    private String requestTime ;
    private String targetId ;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
