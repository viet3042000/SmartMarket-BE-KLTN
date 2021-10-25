package com.smartmarket.code.request;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.request.entity.RoleUpdate;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class UpdateRoleRequest implements Serializable {

    private RoleUpdate role;

}
