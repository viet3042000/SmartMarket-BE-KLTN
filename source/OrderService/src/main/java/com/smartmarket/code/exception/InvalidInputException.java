package com.smartmarket.code.exception;

/**
 * @author HopNX
 */

public class InvalidInputException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String message;
    private String requestId;

    public InvalidInputException(String message, String responseId) {
        this.message = message;
        this.requestId = responseId;

    }

    public InvalidInputException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }



    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
