package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class QueryProductTypeRequest implements Serializable {

    @NotBlank(message = "productProviderName is require")
    @Size(max = 100, message = "productProviderName should be less than or equal to 100 characters")
    private String productProviderName;
}
