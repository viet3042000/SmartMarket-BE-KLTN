package com.smartmarket.code.request;

import com.smartmarket.code.request.entity.RoleUpdate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class UpdateRoleRequest implements Serializable {
    @NotNull(message = "role is required")
    private RoleUpdate role;

}
