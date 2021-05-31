package com.smartmarket.code.request;

import java.io.Serializable;

public class QueryTravelInsuranceBICRequest {
    private Long inquiryType ;
    private String orderId ;
    private String refCode ;

    public Long getInquiryType() {
        return inquiryType;
    }

    public void setInquiryType(Long inquiryType) {
        this.inquiryType = inquiryType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }
}
