package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class CreateProductRequest implements Serializable {
    @NotBlank(message = "productName is require")
    @Size(max = 100, message = "productName should be less than or equal to 100 characters")
    private String productName;

    @NotBlank(message = "productProvider is require")
    @Size(max = 100, message = "productProvider should be less than or equal to 100 characters")
    private String productProvider;

    @NotBlank(message = "type is require")
    @Size(max = 100, message = "type should be less than or equal to 100 characters")
    private String type;

    @NotBlank(message = "price is require")
    @Size(max = 100, message = "price should be less than or equal to 100 characters")
    private String price;

    private String desc;
}
