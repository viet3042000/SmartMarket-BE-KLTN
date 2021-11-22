package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.ItemDetailCreateRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ListItems {
    ArrayList<ItemDetailCreateRequest> orderItems;
}
