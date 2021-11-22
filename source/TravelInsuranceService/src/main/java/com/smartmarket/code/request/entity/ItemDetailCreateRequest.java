package com.smartmarket.code.request.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.smartmarket.code.request.ProductDetailCreateRequest;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ItemDetailCreateRequest implements Serializable {

//    @NotBlank(message = "itemId is require")
//    @Size(max = 100, message = "itemId should be less than or equal to 100 characters")
//    private String itemId;

    @NotBlank(message = "productName is require")
    @Size(max = 100, message = "productName should be less than or equal to 100 characters")
    private String productName;

    @NotBlank(message = "productProvider is require")
    @Size(max = 100, message = "productProvider should be less than or equal to 100 characters")
    private String productProvider;

    @NotBlank(message = "productType is require")
    @Size(max = 100, message = "productType should be less than or equal to 100 characters")
    private String productType;

    @NotNull(message = "productDetailCreateRequest is require")
    private ProductDetailCreateRequest productDetailCreateRequest;

}
