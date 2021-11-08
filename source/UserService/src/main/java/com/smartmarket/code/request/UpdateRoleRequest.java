package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class UpdateRoleRequest implements Serializable {

    @NotNull(message = "roleName is required")
    private String roleName;

    private Integer enabled;

    private String desc;

}
