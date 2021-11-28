package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.ItemDetailCreateRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

@Getter
@Setter
public class CreateOrderRequest implements Serializable {

    @NotNull(message = "orderPrice is require")
    @Min(value = 0)
    private BigDecimal orderPrice;

    @NotNull(message = "orderItems is required")
    private @Valid ArrayList<ItemDetailCreateRequest> orderItems;

}
