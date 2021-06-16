package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import java.io.Serializable;

@Getter
@Setter
public class BaseDetail<T> extends BaseRequest implements Serializable {

//    @NotNull(message = "detail is require")
    private @Valid T  detail ;


}
