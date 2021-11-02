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
public class QueryAllProductRequest implements Serializable {
    @NotNull(message = "page is require")
    @Min(value = 1)
    private int page  ;

    @NotNull(message = "size is require")
    @Min(value = 1)
    private int size ;

//    @NotBlank(message = "state is require")
    @Size(max = 50, message = "state should be less than or equal to 50 characters")
    private String state;
}
