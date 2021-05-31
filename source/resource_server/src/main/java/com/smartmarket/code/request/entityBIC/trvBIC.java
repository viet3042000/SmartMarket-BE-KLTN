package com.smartmarket.code.request.entityBIC;

import java.math.BigDecimal;

public class trvBIC {
    private Long TRVID;
    private Long Orderid;
    private Long AmountPersons;
    private Long AmountDays;
    private BigDecimal SI;
    private BigDecimal Premium;
    private boolean Promotion;
    private String PromotionAddress;
    private Long PeriodTime;
    private String FromDate;
    private String ToDate;
    private String IssueDate;
    private boolean IncludePayer;
    private String Endorsement;
    private Long UserID;
    private Long UserUpproveID;
    private boolean Destroy;
    private boolean Status;
    private boolean WriteByHand;
    private String PrintedPaperNo;
    private String CertificateForm;
    private Long Moduleid ;

    public Long getModuleid() {
        return Moduleid;
    }

    public void setModuleid(Long moduleid) {
        Moduleid = moduleid;
    }

    public Long getTRVID() {
        return TRVID;
    }

    public void setTRVID(Long TRVID) {
        this.TRVID = TRVID;
    }

    public Long getOrderid() {
        return Orderid;
    }

    public void setOrderid(Long orderid) {
        Orderid = orderid;
    }

    public Long getAmountPersons() {
        return AmountPersons;
    }

    public void setAmountPersons(Long amountPersons) {
        AmountPersons = amountPersons;
    }

    public Long getAmountDays() {
        return AmountDays;
    }

    public void setAmountDays(Long amountDays) {
        AmountDays = amountDays;
    }

    public BigDecimal getSI() {
        return SI;
    }

    public void setSI(BigDecimal SI) {
        this.SI = SI;
    }

    public BigDecimal getPremium() {
        return Premium;
    }

    public void setPremium(BigDecimal premium) {
        Premium = premium;
    }

    public boolean isPromotion() {
        return Promotion;
    }

    public void setPromotion(boolean promotion) {
        Promotion = promotion;
    }

    public String getPromotionAddress() {
        return PromotionAddress;
    }

    public void setPromotionAddress(String promotionAddress) {
        PromotionAddress = promotionAddress;
    }

    public Long getPeriodTime() {
        return PeriodTime;
    }

    public void setPeriodTime(Long periodTime) {
        PeriodTime = periodTime;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getToDate() {
        return ToDate;
    }

    public void setToDate(String toDate) {
        ToDate = toDate;
    }

    public String getIssueDate() {
        return IssueDate;
    }

    public void setIssueDate(String issueDate) {
        IssueDate = issueDate;
    }

    public boolean isIncludePayer() {
        return IncludePayer;
    }

    public void setIncludePayer(boolean includePayer) {
        IncludePayer = includePayer;
    }

    public String getEndorsement() {
        return Endorsement;
    }

    public void setEndorsement(String endorsement) {
        Endorsement = endorsement;
    }

    public Long getUserID() {
        return UserID;
    }

    public void setUserID(Long userID) {
        UserID = userID;
    }

    public Long getUserUpproveID() {
        return UserUpproveID;
    }

    public void setUserUpproveID(Long userUpproveID) {
        UserUpproveID = userUpproveID;
    }

    public boolean isDestroy() {
        return Destroy;
    }

    public void setDestroy(boolean destroy) {
        Destroy = destroy;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public boolean isWriteByHand() {
        return WriteByHand;
    }

    public void setWriteByHand(boolean writeByHand) {
        WriteByHand = writeByHand;
    }

    public String getPrintedPaperNo() {
        return PrintedPaperNo;
    }

    public void setPrintedPaperNo(String printedPaperNo) {
        PrintedPaperNo = printedPaperNo;
    }

    public String getCertificateForm() {
        return CertificateForm;
    }

    public void setCertificateForm(String certificateForm) {
        CertificateForm = certificateForm;
    }
}
