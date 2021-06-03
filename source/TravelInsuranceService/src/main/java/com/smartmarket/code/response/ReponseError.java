package com.smartmarket.code.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReponseError {

    private String responseId;
    private String responseTime;
    private String resultCode;
    private String resultMessage;
    private String detailErrorCode;
    private String detailErrorMessage;

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
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
}
