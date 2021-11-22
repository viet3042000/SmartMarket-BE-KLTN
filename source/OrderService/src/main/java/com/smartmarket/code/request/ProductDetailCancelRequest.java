package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.OrderUpdate;
import com.smartmarket.code.request.entity.TRVDetailUpdate;
import com.smartmarket.code.request.entity.TRVUpdate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Getter
@Setter
public class ProductDetailCancelRequest {

    @NotNull(message = "orders is required")
    private @Valid OrderUpdate orders;

    @NotNull(message = "trv is required")
    private @Valid TRVUpdate trv;

    @NotNull(message = "trvDetails is required")
    private @Valid ArrayList<TRVDetailUpdate> trvDetails;

}
