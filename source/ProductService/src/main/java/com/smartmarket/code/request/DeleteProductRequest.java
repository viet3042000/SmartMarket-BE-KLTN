package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class DeleteProductRequest implements Serializable {
    @Size(max = 30, message = "userName should be less than or equal to 30 characters")
    private String userName;

    @NotBlank(message = "productName is require")
    @Size(max = 100, message = "productName should be less than or equal to 100 characters")
    private String productName;
}
