package com.smartmarket.code.request.entityBIC;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateTravelInsuranceToBIC {
    private OrdersUpdateBIC Orders ;
    private trvUpdateBIC TRV ;
    private List<trvDetailUpdateBIC> TRVDetail;
}
