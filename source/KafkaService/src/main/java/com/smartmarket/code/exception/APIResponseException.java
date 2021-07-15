package com.smartmarket.code.exception;

import org.springframework.http.HttpStatus;

public class APIResponseException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private String responseId;
    private String resultCode;
    private String resultMessage;
    private HttpStatus detailErrorCode;
    private String detailErrorMessage;

    public APIResponseException(String responseId ,String resultCode , String resultMessage ,
                                HttpStatus detailErrorCode, String detailErrorMessage) {
        this.responseId = responseId;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.detailErrorCode = detailErrorCode;
        this.detailErrorMessage = detailErrorMessage;
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

    public HttpStatus getDetailErrorCode() {
        return detailErrorCode;
    }

    public void setDetailErrorCode(HttpStatus detailErrorCode) {
        this.detailErrorCode = detailErrorCode;
    }

    public String getDetailErrorMessage() {
        return detailErrorMessage;
    }

    public void setDetailErrorMessage(String detailErrorMessage) {
        this.detailErrorMessage = detailErrorMessage;
    }



}
