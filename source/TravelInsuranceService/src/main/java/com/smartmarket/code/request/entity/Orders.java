package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Orders implements Serializable {

    private String refCode ;
    private Long orderId ;
    private Long ordCustId ;
    private String ordCustMessage;
    private String ordBillFirstName;
    private String ordBillMobile;
    private String ordBillStreet1;
    private String ordBillEmail;

    private String ordDate;

    private Long ordStatus;
    private String productId;
    private Long ordTotalQty;
    private Long orderPaymentMethod;
    private String orderShipModule;
    private Long ordIsDigital;
    private String ordToken;
    private BigDecimal ordPaidMoney ;
    private BigDecimal ordTotal ;
    private BigDecimal ordDiscountAmount ;
    private String ordSource ;
    private Long userId ;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public Long getOrdCustId() {
        return ordCustId;
    }

    public void setOrdCustId(Long ordCustId) {
        this.ordCustId = ordCustId;
    }

    public String getOrdCustMessage() {
        return ordCustMessage;
    }

    public void setOrdCustMessage(String ordCustMessage) {
        this.ordCustMessage = ordCustMessage;
    }

    public String getOrdBillFirstName() {
        return ordBillFirstName;
    }

    public void setOrdBillFirstName(String ordBillFirstName) {
        this.ordBillFirstName = ordBillFirstName;
    }

    public String getOrdBillMobile() {
        return ordBillMobile;
    }

    public void setOrdBillMobile(String ordBillMobile) {
        this.ordBillMobile = ordBillMobile;
    }

    public String getOrdBillStreet1() {
        return ordBillStreet1;
    }

    public void setOrdBillStreet1(String ordBillStreet1) {
        this.ordBillStreet1 = ordBillStreet1;
    }

    public String getOrdBillEmail() {
        return ordBillEmail;
    }

    public void setOrdBillEmail(String ordBillEmail) {
        this.ordBillEmail = ordBillEmail;
    }

    public String getOrdDate() {
        return ordDate;
    }

    public void setOrdDate(String ordDate) {
        this.ordDate = ordDate;
    }

    public Long getOrdStatus() {
        return ordStatus;
    }

    public void setOrdStatus(Long ordStatus) {
        this.ordStatus = ordStatus;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Long getOrdTotalQty() {
        return ordTotalQty;
    }

    public void setOrdTotalQty(Long ordTotalQty) {
        this.ordTotalQty = ordTotalQty;
    }

    public Long getOrderPaymentMethod() {
        return orderPaymentMethod;
    }

    public void setOrderPaymentMethod(Long orderPaymentMethod) {
        this.orderPaymentMethod = orderPaymentMethod;
    }

    public String getOrderShipModule() {
        return orderShipModule;
    }

    public void setOrderShipModule(String orderShipModule) {
        this.orderShipModule = orderShipModule;
    }

    public Long getOrdIsDigital() {
        return ordIsDigital;
    }

    public void setOrdIsDigital(Long ordIsDigital) {
        this.ordIsDigital = ordIsDigital;
    }

    public String getOrdToken() {
        return ordToken;
    }

    public void setOrdToken(String ordToken) {
        this.ordToken = ordToken;
    }

    public BigDecimal getOrdPaidMoney() {
        return ordPaidMoney;
    }

    public void setOrdPaidMoney(BigDecimal ordPaidMoney) {
        this.ordPaidMoney = ordPaidMoney;
    }

    public BigDecimal getOrdTotal() {
        return ordTotal;
    }

    public void setOrdTotal(BigDecimal ordTotal) {
        this.ordTotal = ordTotal;
    }


    public BigDecimal getOrdDiscountAmount() {
        return ordDiscountAmount;
    }

    public void setOrdDiscountAmount(BigDecimal ordDiscountAmount) {
        this.ordDiscountAmount = ordDiscountAmount;
    }

    public String getOrdSource() {
        return ordSource;
    }

    public void setOrdSource(String ordSource) {
        this.ordSource = ordSource;
    }
}
