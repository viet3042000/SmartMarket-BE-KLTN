package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.ItemDetailCreateRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@Getter
@Setter
public class ListCreateItems {
    @NotNull(message = "orderItems is required")
    ArrayList<ItemDetailCreateRequest> orderItems;
}
