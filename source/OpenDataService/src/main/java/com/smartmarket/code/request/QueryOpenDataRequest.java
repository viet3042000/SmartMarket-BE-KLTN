package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class QueryOpenDataRequest implements Serializable {

    @NotNull(message = "q is required")
    private String q ;

    @NotNull(message = "start is required")
    private Long start ;

    @NotNull(message = "rows is required")
    private Long rows ;

}
