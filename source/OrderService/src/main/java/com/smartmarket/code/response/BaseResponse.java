package com.smartmarket.code.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class BaseResponse implements Serializable {
    private String responseId;
    private String orderId;
    private String responseTime;
    private String resultCode;
    private String resultMessage;
    private Object detail;
}
