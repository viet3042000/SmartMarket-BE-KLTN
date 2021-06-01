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
    private String responseTime;
    private String resultCode;
    private String resultMessage;
    private String detailErrorCode;
    private String detailErrorMessage;



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

}
