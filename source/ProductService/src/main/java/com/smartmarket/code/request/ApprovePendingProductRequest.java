package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class ApprovePendingProductRequest implements Serializable {

    @NotBlank(message = "productName is require")
    @Size(max = 100, message = "productName should be less than or equal to 100 characters")
    private String productName;

    @NotBlank(message = "state is require")
    @Size(max = 50, message = "state should be less than or equal to 50 characters")
    private String state;
}
