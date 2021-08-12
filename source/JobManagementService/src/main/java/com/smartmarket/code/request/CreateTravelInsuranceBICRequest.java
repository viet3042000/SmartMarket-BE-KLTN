package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.Orders;
import com.smartmarket.code.request.entity.TRV;
import com.smartmarket.code.request.entity.TRVDetail;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;


@Getter
@Setter
public class CreateTravelInsuranceBICRequest implements Serializable {

    @NotNull(message = "orders is required")
    private @Valid Orders orders;

    @NotNull(message = "trv is required")
    private @Valid TRV trv;

    @NotNull(message = "trvDetails is required")
    private @Valid ArrayList<TRVDetail> trvDetails;

}
