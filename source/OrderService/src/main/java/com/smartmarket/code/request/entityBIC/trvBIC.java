package com.smartmarket.code.request.entityBIC;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
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
    private String FromZoneGuid ;
    private String ToZoneGuid ;

}
