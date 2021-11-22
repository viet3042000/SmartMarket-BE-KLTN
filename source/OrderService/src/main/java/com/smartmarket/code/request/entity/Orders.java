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
public class Orders implements Serializable {
    //set after create succeeded (for aborting can get from requestpayload)
    @Size(max = 50, message = "orderReference should be less than or equal to 50 characters")
    private String orderReference ;

    private String orderId ;

    private Long ordCustId ;

    private String ordCustMessage;

    @NotBlank(message = "ordBillFirstName is require")
    @Size(max = 255, message = "ordBillFirstName should be less than or equal to 255 characters")
//    @JsonDeserialize(using = StringOnlyDeserializer.class)
    private String ordBillFirstName;

    @NotBlank(message = "ordBillMobile is require")
    @Size(max = 50, message = "ordBillMobile should be less than or equal to 50 characters")
    @Pattern(regexp="(^$|[0-9]{9,12})")
    private String ordBillMobile;

    @NotNull(message = "ordBillStreet1 is require")
    @Size(max = 255, message = "ordBillStreet1 should be less than or equal to 255 characters")
    private String ordBillStreet1;

    @NotBlank(message = "ordBillEmail is require")
    @Size(max =250, message = "ordBillEmail should be less than or equal to 250 characters")
    @Email
    private String ordBillEmail;

    @NotBlank(message = "ordDate is require")
    @ValidDate(message = "ordDate is invalid date format (yyyy-MM-dd'T'HH:ss:mm)")
    private String ordDate;

    @Range(min= 1, max= 2)
    @NotNull(message = "ordStatus is require")
    private Long ordStatus;

    private String productId;

    @NotNull(message = "ordTotalQty is require")
    @Min(value = 0)
    private Long ordTotalQty;

    @NotNull(message = "orderPaymentMethod is require")
    private Long orderPaymentMethod;

    private String orderShipModule;

    private Long ordIsDigital;

    private String ordToken;

    @NotNull(message = "ordPaidMoney is require")
    @Min(value = 0)
    private BigDecimal ordPaidMoney ;

    private BigDecimal ordTotal ;

    @NotNull(message = "ordDiscountAmount is require")
    @Min(value = 0)
    private BigDecimal ordDiscountAmount ;

    @Size(max =50, message = "ordSource should be less than or equal to 50 characters")
    private String ordSource ;

}
