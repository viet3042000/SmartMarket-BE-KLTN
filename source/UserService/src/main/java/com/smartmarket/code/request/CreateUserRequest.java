package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.UserCreate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
public class CreateUserRequest implements Serializable {
    @NotNull(message = "UserCreate is required")
    private UserCreate user;

    @NotNull(message = "role is required")
    private String role ;

}
