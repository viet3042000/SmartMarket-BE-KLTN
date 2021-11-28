package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class ApprovePendingProductRequest implements Serializable {

    @NotBlank(message = "flowName is require")
    @Size(max = 100, message = "flowName should be less than or equal to 100 characters")
    private String flowName;

    @NotNull(message = "productId is require")
    @Min(value = 0)
    private Long productId;

    @NotBlank(message = "decision is require")
    @Size(max = 50, message = "decision should be less than or equal to 50 characters")
    private String decision;
}
