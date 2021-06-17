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
public class Orders implements Serializable {

    @NotEmpty(message = "orderReference is require")
    private String orderReference ;

    private Long orderId ;

    private Long ordCustId ;

    private String ordCustMessage;

    @NotEmpty(message = "ordBillFirstName is require")
    private String ordBillFirstName;

    @NotEmpty(message = "ordBillMobile is require")
    private String ordBillMobile;

    @NotEmpty(message = "ordBillStreet1 is require")
    private String ordBillStreet1;

    @NotEmpty(message = "ordBillEmail is require")
    private String ordBillEmail;

    @NotEmpty(message = "ordDate is require")
    private String ordDate;

    @NotNull(message = "ordStatus is require")
    private Long ordStatus;

    private String productId;

    @NotNull(message = "ordTotalQty is require")
    private Long ordTotalQty;

    @NotNull(message = "orderPaymentMethod is require")
    private Long orderPaymentMethod;

    private String orderShipModule;

    private Long ordIsDigital;

    private String ordToken;

    @NotNull(message = "ordPaidMoney is require")
    private BigDecimal ordPaidMoney ;

    private BigDecimal ordTotal ;

    @NotNull(message = "ordDiscountAmount is require")
    private BigDecimal ordDiscountAmount ;

    @NotEmpty(message = "ordSource is require")
    private String ordSource ;

    private Long userId ;

}
