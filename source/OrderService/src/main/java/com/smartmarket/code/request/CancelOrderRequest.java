package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
public class CancelOrderRequest implements Serializable {

    @NotBlank(message = "orderId is require")
    @Size(max = 100, message = "orderId should be less than or equal to 100 characters")
    private String orderId ;

//    @NotNull(message = "orderItems is required")
    private @Valid ArrayList<ItemDetailCancelRequest> orderItems;

}
