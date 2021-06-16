package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class QueryTravelInsuranceBICRequest implements Serializable {
    private Long inquiryType ;
    private String orderId ;
    private String orderReference ;

}
