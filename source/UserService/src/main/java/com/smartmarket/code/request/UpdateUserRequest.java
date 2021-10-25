package com.smartmarket.code.request;

import com.smartmarket.code.model.User;
import com.smartmarket.code.request.entity.UserUpdate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;

@Getter
@Setter
public class UpdateUserRequest implements Serializable {
    @NotNull(message = "user is required")
    private UserUpdate user;

    private String role;


}
