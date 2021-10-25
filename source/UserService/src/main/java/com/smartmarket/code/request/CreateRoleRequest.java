package com.smartmarket.code.request;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class CreateRoleRequest implements Serializable {
    @NotNull(message = "role is required")
    private Role role;

}
