package com.smartmarket.code.request.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
public class User {

    @NotNull(message = "not null")
    @NotBlank(message = "not blank")
    @NotEmpty(message = "not empty")
    private String userId ;

}
