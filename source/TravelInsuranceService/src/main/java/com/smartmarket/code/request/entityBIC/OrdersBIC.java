package com.smartmarket.code.request.entityBIC;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrdersBIC {

    private String OrderReference;
    private Long Orderid;
    private Long Ordcustid;
    private String Ordcustmessage;
    private String Ordbillfirstname;
    private String Ordbillmobile;
    private String Ordbillstreet1;
    private String Ordbillemail;
    private String Orddate;
    private Long Ordstatus;
    private String ProductId;
    private Long Ordtotalqty;
    private Long Orderpaymentmethod;
    private String Ordershipmodule;
    private Long Ordisdigital;
    private String Ordtoken;
    private BigDecimal Ordpaidmoney;
    private BigDecimal Ordtotal;
    private BigDecimal Orddiscountamount;
    private Long UserId ;

    public String getOrderReference() {
        return OrderReference;
    }

    public void setOrderReference(String orderReference) {
        OrderReference = orderReference;
    }

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    public Long getOrderid() {
        return Orderid;
    }

    public void setOrderid(Long orderid) {
        Orderid = orderid;
    }

    public Long getOrdcustid() {
        return Ordcustid;
    }

    public void setOrdcustid(Long ordcustid) {
        Ordcustid = ordcustid;
    }

    public String getOrdcustmessage() {
        return Ordcustmessage;
    }

    public void setOrdcustmessage(String ordcustmessage) {
        Ordcustmessage = ordcustmessage;
    }

    public String getOrdbillfirstname() {
        return Ordbillfirstname;
    }

    public void setOrdbillfirstname(String ordbillfirstname) {
        Ordbillfirstname = ordbillfirstname;
    }

    public String getOrdbillmobile() {
        return Ordbillmobile;
    }

    public void setOrdbillmobile(String ordbillmobile) {
        Ordbillmobile = ordbillmobile;
    }

    public String getOrdbillstreet1() {
        return Ordbillstreet1;
    }

    public void setOrdbillstreet1(String ordbillstreet1) {
        Ordbillstreet1 = ordbillstreet1;
    }

    public String getOrdbillemail() {
        return Ordbillemail;
    }

    public void setOrdbillemail(String ordbillemail) {
        Ordbillemail = ordbillemail;
    }

    public String getOrddate() {
        return Orddate;
    }

    public void setOrddate(String orddate) {
        Orddate = orddate;
    }

    public Long getOrdstatus() {
        return Ordstatus;
    }

    public void setOrdstatus(Long ordstatus) {
        Ordstatus = ordstatus;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public Long getOrdtotalqty() {
        return Ordtotalqty;
    }

    public void setOrdtotalqty(Long ordtotalqty) {
        Ordtotalqty = ordtotalqty;
    }

    public Long getOrderpaymentmethod() {
        return Orderpaymentmethod;
    }

    public void setOrderpaymentmethod(Long orderpaymentmethod) {
        Orderpaymentmethod = orderpaymentmethod;
    }

    public String getOrdershipmodule() {
        return Ordershipmodule;
    }

    public void setOrdershipmodule(String ordershipmodule) {
        Ordershipmodule = ordershipmodule;
    }

    public Long getOrdisdigital() {
        return Ordisdigital;
    }

    public void setOrdisdigital(Long ordisdigital) {
        Ordisdigital = ordisdigital;
    }

    public String getOrdtoken() {
        return Ordtoken;
    }

    public void setOrdtoken(String ordtoken) {
        Ordtoken = ordtoken;
    }

    public BigDecimal getOrdpaidmoney() {
        return Ordpaidmoney;
    }

    public void setOrdpaidmoney(BigDecimal ordpaidmoney) {
        Ordpaidmoney = ordpaidmoney;
    }

    public BigDecimal getOrdtotal() {
        return Ordtotal;
    }

    public void setOrdtotal(BigDecimal ordtotal) {
        Ordtotal = ordtotal;
    }

    public BigDecimal getOrddiscountamount() {
        return Orddiscountamount;
    }

    public void setOrddiscountamount(BigDecimal orddiscountamount) {
        Orddiscountamount = orddiscountamount;
    }
}
