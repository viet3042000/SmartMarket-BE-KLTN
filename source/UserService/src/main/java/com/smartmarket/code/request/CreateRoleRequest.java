package com.smartmarket.code.request;

import com.smartmarket.code.model.Role;
import com.smartmarket.code.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class CreateRoleRequest implements Serializable {
    @NotBlank(message = "roleName is require")
    @Size(max = 30, message = "roleName should be less than or equal to 30 characters")
    private String roleName;

    private Integer enabled;

    private String desc;

}
