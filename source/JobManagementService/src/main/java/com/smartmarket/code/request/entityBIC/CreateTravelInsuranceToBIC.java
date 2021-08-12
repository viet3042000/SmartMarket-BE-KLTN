package com.smartmarket.code.request.entityBIC;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class CreateTravelInsuranceToBIC {

    private OrdersBIC Orders ;
    private trvBIC TRV ;
    private List<trvDetailBIC> TRVDetail;

}
