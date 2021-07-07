package com.smartmarket.code.exception;


import org.json.JSONObject;
import org.springframework.http.HttpStatus;

/**
 * @author HopNX
 */


public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String detailErrorMessage;
    private HttpStatus httpStatusDetailCode;
    private String httpStatusCode;
    private String errorMessage;
    private String responseId;
    private JSONObject responseBackend;

    public CustomException(String detailErrorMessage, HttpStatus httpStatusDetailCode, String responseId, JSONObject responseBackend
                            , String httpStatusCode, String errorMessage ) {
        this.detailErrorMessage = detailErrorMessage;
        this.httpStatusDetailCode = httpStatusDetailCode;
        this.responseId = responseId;
        this.responseBackend = responseBackend;
        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;

    }

    public CustomException(String detailErrorMessage, HttpStatus httpStatusDetailCode, String responseId, JSONObject responseBackend) {
        this.detailErrorMessage = detailErrorMessage;
        this.httpStatusDetailCode = httpStatusDetailCode;
        this.responseId = responseId;
        this.responseBackend = responseBackend;

    }


    public CustomException(String detailErrorMessage, HttpStatus httpStatus, String responseId) {
        this.detailErrorMessage = detailErrorMessage;
        this.httpStatusDetailCode = httpStatus;
        this.responseId = responseId;

    }

    public CustomException(String detailErrorMessage, HttpStatus httpStatus, String responseId,String httpStatusCode, String errorMessage) {
        this.detailErrorMessage = detailErrorMessage;
        this.httpStatusDetailCode = httpStatus;
        this.responseId = responseId;
        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;

    }

    public CustomException(String detailErrorMessage, HttpStatus httpStatus) {
        this.detailErrorMessage = detailErrorMessage;
        this.httpStatusDetailCode = httpStatus;
    }

    public String getDetailErrorMessage() {
        return detailErrorMessage;
    }

    public void setDetailErrorMessage(String detailErrorMessage) {
        this.detailErrorMessage = detailErrorMessage;
    }

    public HttpStatus getHttpStatusDetailCode() {
        return httpStatusDetailCode;
    }

    public void setHttpStatusDetailCode(HttpStatus httpStatusDetailCode) {
        this.httpStatusDetailCode = httpStatusDetailCode;
    }

    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public JSONObject getResponseBackend() {
        return responseBackend;
    }

    public void setResponseBackend(JSONObject responseBackend) {
        this.responseBackend = responseBackend;
    }
}
