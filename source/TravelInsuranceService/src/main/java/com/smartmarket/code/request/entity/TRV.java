package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class TRV implements Serializable {

    private Long trvId ;
    private String orderId;

    @NotNull(message = "amountPersons is require")
    private Long amountPersons;

    @NotNull(message = "amountDays is require")
    private Long amountDays;

    private BigDecimal si;

    private BigDecimal premium;

    @NotNull(message = "promotion is require")
    private Long promotion;

    @NotNull(message = "promotionAddress is require")
    private String promotionAddress;

    private String periodTime;

    @NotNull(message = "fromDate is require")
    private String fromDate;

    @NotNull(message = "toDate is require")
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
