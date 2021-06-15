package com.smartmarket.code.exception;


import org.springframework.http.HttpStatus;

/**
 * @author HopNX
 */


public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String message;
    private final HttpStatus httpStatus;
    private String responseId;

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
}
