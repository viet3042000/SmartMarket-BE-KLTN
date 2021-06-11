package com.smartmarket.code.request;

import java.io.Serializable;

public class QueryTravelInsuranceBICRequest {
    private Long inquiryType ;
    private String orderId ;
    private String orderReference ;

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

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }
}
