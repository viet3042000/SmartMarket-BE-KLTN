package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class BaseJobRequest implements Serializable {

    @NotBlank(message = "requestId is require")
    @Size(max = 36, message = "requestId should be less than or equal to 36 characters")
    private String requestId;

}
