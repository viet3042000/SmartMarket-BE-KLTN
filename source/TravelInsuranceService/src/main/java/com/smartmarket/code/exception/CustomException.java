package com.smartmarket.code.exception;


import com.smartmarket.code.util.EJson;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

/**
 * @author HopNX
 */


public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String message;
    private final HttpStatus httpStatus;
    private String responseId;
    private JSONObject responseBIC ;

    public CustomException(String message, HttpStatus httpStatus, String responseId ,JSONObject responseBIC ) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.responseId = responseId;
        this.responseBIC = responseBIC ;

    }


    public CustomException(String message, HttpStatus httpStatus, String responseId) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.responseId = responseId;

    }

    public CustomException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public JSONObject getResponseBIC() {
        return responseBIC;
    }

    public void setResponseBIC(JSONObject responseBIC) {
        this.responseBIC = responseBIC;
    }
}
