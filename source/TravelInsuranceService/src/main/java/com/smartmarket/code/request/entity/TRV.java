package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
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


}
