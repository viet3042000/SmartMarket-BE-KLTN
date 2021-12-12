package com.smartmarket.code.request.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class DeleteUserRequest implements Serializable {

    @NotNull(message = "userName is required")
    private String userName;

}
