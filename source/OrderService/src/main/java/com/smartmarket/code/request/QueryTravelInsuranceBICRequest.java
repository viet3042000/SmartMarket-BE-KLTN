package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class QueryTravelInsuranceBICRequest implements Serializable {

    @NotNull(message = "inquiryType is required")
    private Long inquiryType ;

    private String orderId ;

    private String orderReference ;

}
