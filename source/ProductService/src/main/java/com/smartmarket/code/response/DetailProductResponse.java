package com.smartmarket.code.response;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;

@Getter
@Setter
public class DetailProductResponse {

    private Long id;

    private String productName;

    private String type;

    private String productProvider;

    private String desc;

    private String price;

}
