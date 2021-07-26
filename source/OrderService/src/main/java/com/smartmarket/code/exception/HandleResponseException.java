package com.smartmarket.code.exception;

import org.springframework.http.HttpStatus;

public class HandleResponseException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private final String message;
    private final HttpStatus httpStatus;

    private String responseId;
    private String resultCode;
    private String resultMessage;
    private String detailErrorCode;
    private String detailErrorMessage;


    public HandleResponseException(String message, HttpStatus httpStatus ,String responseId ,
                           String resultCode , String resultMessage , String detailErrorCode,String detailErrorMessage) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.responseId = responseId;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.detailErrorCode = detailErrorCode;
        this.detailErrorMessage = detailErrorMessage;
    }

    public HandleResponseException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getDetailErrorCode() {
        return detailErrorCode;
    }

    public void setDetailErrorCode(String detailErrorCode) {
        this.detailErrorCode = detailErrorCode;
    }

    public String getDetailErrorMessage() {
        return detailErrorMessage;
    }

    public void setDetailErrorMessage(String detailErrorMessage) {
        this.detailErrorMessage = detailErrorMessage;
    }



    @Override
    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }


}
