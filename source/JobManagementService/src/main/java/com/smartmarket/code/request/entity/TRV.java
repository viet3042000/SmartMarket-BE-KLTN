package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartmarket.code.annotation.ValidDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class TRV implements Serializable {

    private Long trvId ;
    private String orderId;

    @NotNull(message = "amountPersons is require")
    @Range(min= 1, max= 100)
    private Long amountPersons;

    @NotNull(message = "amountDays is require")
    @Min(value = 0)
    private Long amountDays;

    private BigDecimal si;

    private BigDecimal premium;

    @NotNull(message = "promotion is require")
    private Long promotion;

    @NotNull(message = "promotionAddress is require")
    @Size(max =255, message = "promotionAddress should be less than or equal to 255 characters")
    private String promotionAddress;

    private String periodTime;

    @NotBlank(message = "fromDate is require")
    @ValidDate(message = "fromDate is invalid date format (yyyy-MM-dd'T'HH:ss:mm)")
    private String fromDate;

    @NotBlank(message = "toDate is require")
    @ValidDate(message = "toDate is invalid date format (yyyy-MM-dd'T'HH:ss:mm)")
    private String toDate;

    private String issueDate;

    private Long includePayer;

    private String endorsement;

    private Long userID;

    private Long userUpproveID;

    @Range(min= 0, max= 1)
    private Long destroy;

    private Long status;

    private Long writeByHand;

    private String printedPaperNo;

    private String certificateForm;

    private Long moduleId ;


}
