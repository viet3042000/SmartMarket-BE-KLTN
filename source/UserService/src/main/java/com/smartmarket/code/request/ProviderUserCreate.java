package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.UserCreate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class ProviderUserCreate extends UserCreate {
    //BIC
    @NotNull(message = "productProviderName is require")
    @Size(max = 100, message = "productProviderName should be less than or equal to 100 characters")
    private String productProviderName;

    private String desc;
}
