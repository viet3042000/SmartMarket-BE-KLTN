package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TRV implements Serializable {

    private Long trvId ;
    private String orderId;
    private Long amountPersons;
    private Long amountDays;
    private BigDecimal si;
    private BigDecimal premium;
    private Long promotion;
    private String promotionAddress;
    private String periodTime;
    private String fromDate;
    private String toDate;
    private String issueDate;
    private Long includePayer;
    private String endorsement;
    private Long userID;
    private Long userUpproveID;
    private Long destroy;
    private Long status;
    private Long writeByHand;
    private String printedPaperNo;
    private String certificateForm;
    private Long moduleId ;

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public Long getTrvId() {
        return trvId;
    }

    public void setTrvId(Long trvId) {
        this.trvId = trvId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getAmountPersons() {
        return amountPersons;
    }

    public void setAmountPersons(Long amountPersons) {
        this.amountPersons = amountPersons;
    }

    public Long getAmountDays() {
        return amountDays;
    }

    public void setAmountDays(Long amountDays) {
        this.amountDays = amountDays;
    }

    public BigDecimal getSi() {
        return si;
    }

    public void setSi(BigDecimal si) {
        this.si = si;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public Long getPromotion() {
        return promotion;
    }

    public void setPromotion(Long promotion) {
        this.promotion = promotion;
    }

    public String getPromotionAddress() {
        return promotionAddress;
    }

    public void setPromotionAddress(String promotionAddress) {
        this.promotionAddress = promotionAddress;
    }

    public String getPeriodTime() {
        return periodTime;
    }

    public void setPeriodTime(String periodTime) {
        this.periodTime = periodTime;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public Long getIncludePayer() {
        return includePayer;
    }

    public void setIncludePayer(Long includePayer) {
        this.includePayer = includePayer;
    }

    public String getEndorsement() {
        return endorsement;
    }

    public void setEndorsement(String endorsement) {
        this.endorsement = endorsement;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getUserUpproveID() {
        return userUpproveID;
    }

    public void setUserUpproveID(Long userUpproveID) {
        this.userUpproveID = userUpproveID;
    }

    public Long getDestroy() {
        return destroy;
    }

    public void setDestroy(Long destroy) {
        this.destroy = destroy;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getWriteByHand() {
        return writeByHand;
    }

    public void setWriteByHand(Long writeByHand) {
        this.writeByHand = writeByHand;
    }

    public String getPrintedPaperNo() {
        return printedPaperNo;
    }

    public void setPrintedPaperNo(String printedPaperNo) {
        this.printedPaperNo = printedPaperNo;
    }

    public String getCertificateForm() {
        return certificateForm;
    }

    public void setCertificateForm(String certificateForm) {
        this.certificateForm = certificateForm;
    }
}
