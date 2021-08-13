package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class BaseJobDetail<T> extends BaseJobRequest implements Serializable {

    @NotNull(message = "detail is required")
    private @Valid T  detail ;

}