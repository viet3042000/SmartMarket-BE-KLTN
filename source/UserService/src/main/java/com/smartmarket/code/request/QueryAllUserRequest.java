package com.smartmarket.code.request;

import com.smartmarket.code.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
public class QueryAllUserRequest implements Serializable {
    @NotNull(message = "page is require")
    @Min(value = 1)
    private int page  ;

    @NotNull(message = "size is require")
    @Min(value = 1)
    private int size ;

    //condition.

}
