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

    @NotNull(message = "orderReference is require")
    private String orderReference ;

    private String orderId ;

    private Long ordCustId ;

    private String ordCustMessage;

    @NotNull(message = "ordBillFirstName is require")
    private String ordBillFirstName;

    @NotNull(message = "ordBillMobile is require")
    private String ordBillMobile;

    @NotNull(message = "ordBillStreet1 is require")
    private String ordBillStreet1;

    @NotNull(message = "ordBillEmail is require")
    private String ordBillEmail;

    @NotNull(message = "ordDate is require")
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

    private String ordSource ;

    private Long userId ;

}
