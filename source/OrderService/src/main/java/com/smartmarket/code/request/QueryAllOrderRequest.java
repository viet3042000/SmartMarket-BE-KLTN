package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.QueryConditions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class QueryAllOrderRequest implements Serializable {
    @NotNull(message = "page is require")
    @Min(value = 1)
    private int page  ;

    @NotNull(message = "size is require")
    @Min(value = 1)
    private int size ;

    @NotNull(message = "conditions is required")
    private @Valid QueryConditions conditions;

}
