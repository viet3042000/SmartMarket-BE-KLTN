package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.Orders;
import com.smartmarket.code.request.entity.TRV;
import com.smartmarket.code.request.entity.TRVDetail;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;


@Getter
@Setter
public class CreateTravelInsuranceBICRequest implements Serializable {

//    @NotNull(message = "abc")
    private Orders orders;

    private TRV trv;
    private ArrayList<TRVDetail> trvDetails;

}
