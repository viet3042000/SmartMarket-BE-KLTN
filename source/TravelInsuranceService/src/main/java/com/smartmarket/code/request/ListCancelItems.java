package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.ItemDetailCancelRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Getter
@Setter
public class ListCancelItems {
    @NotNull(message = "orderItems is required")
    ArrayList<ItemDetailCancelRequest> orderItems;
}
