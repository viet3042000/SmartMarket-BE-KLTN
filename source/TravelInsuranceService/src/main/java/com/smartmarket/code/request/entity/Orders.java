package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class Orders implements Serializable {

//    @NotNull(message = "test")
    private String orderReference ;

    private Long orderId ;
    private Long ordCustId ;
    private String ordCustMessage;
    private String ordBillFirstName;
    private String ordBillMobile;
    private String ordBillStreet1;
    private String ordBillEmail;

    private String ordDate;

    private Long ordStatus;
    private String productId;
    private Long ordTotalQty;
    private Long orderPaymentMethod;
    private String orderShipModule;
    private Long ordIsDigital;
    private String ordToken;
    private BigDecimal ordPaidMoney ;
    private BigDecimal ordTotal ;
    private BigDecimal ordDiscountAmount ;
    private String ordSource ;
    private Long userId ;

}
