package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class BaseRequest implements Serializable {

    @NotNull(message = "requestId is require")
    @Size(max =36, message = "requestId should be less than or equal to 36 characters")
    private String requestId ;

    @NotNull(message = "requestTime is require")
    @Size(max =14, message = "requestId should be less than or equal to 14 characters")
    private String requestTime ;

    @NotNull(message = "targetId is require")
    @Size(max =10, message = "targetId should be less than or equal to 14 characters")
    private String targetId ;


}
