package com.smartmarket.code.response;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreateTravelInsuranceBICResponse implements Serializable {
    private boolean succeeded;
    private String orderId ;
    private DataCreateBIC data ;

}
