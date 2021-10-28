package com.smartmarket.code.request;

import com.smartmarket.code.model.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class CreateProductTypeRequest implements Serializable {

    @NotBlank(message = "productTypeName is require")
    @Size(max = 100, message = "productTypeName should be less than or equal to 100 characters")
    private String productTypeName;

    private String desc;

}
