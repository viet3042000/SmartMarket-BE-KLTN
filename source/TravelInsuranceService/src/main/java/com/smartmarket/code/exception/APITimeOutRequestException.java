package com.smartmarket.code.exception;

public class APITimeOutRequestException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String responseId;
    private String resultCode;
    private String resultMessage;
    private String detailErrorMessage;

    public APITimeOutRequestException(String responseId, String resultCode, String resultMessage,String detailErrorMessage) {
        this.responseId = responseId;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
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


    public String getDetailErrorMessage() {
        return detailErrorMessage;
    }

    public void setDetailErrorMessage(String detailErrorMessage) {
        this.detailErrorMessage = detailErrorMessage;
    }


}
