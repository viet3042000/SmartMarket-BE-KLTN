package com.smartmarket.code.request.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;


@Getter
@Setter
public class RoleUpdate {

    @NotNull(message = "roleName is required")
    private String roleName;

    private Integer enabled;

    private String desc;

}
