package com.smartmarket.code.request.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;


@Getter
@Setter
public class Role {

    private Long id;

    private String roleName;

    private Long enabled;

}
