package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DeleteRoleRequest implements Serializable {

//    private Long id;

    private String roleName;

}
