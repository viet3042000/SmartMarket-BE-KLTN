package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class BaseRequest implements Serializable {

    @NotNull(message = "requestId is require")
    private String requestId ;

    @NotNull(message = "requestTime is require")
    private String requestTime ;

    @NotNull(message = "targetId is require")
    private String targetId ;


}
