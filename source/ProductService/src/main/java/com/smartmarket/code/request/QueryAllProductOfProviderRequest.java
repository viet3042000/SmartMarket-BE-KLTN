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
public class QueryAllProductOfProviderRequest implements Serializable {
    @NotNull(message = "page is require")
    @Min(value = 1)
    private int page  ;

    @NotNull(message = "size is require")
    @Min(value = 1)
    private int size ;
}
