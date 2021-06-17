package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class QueryTravelInsuranceBICRequest implements Serializable {

    @NotNull(message = "orders is required")
    private Long inquiryType ;

    @NotNull(message = "orderId is required")
    private String orderId ;

    @NotNull(message = "orderReference is required")
    private String orderReference ;

}
