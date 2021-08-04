package com.smartmarket.code.request.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserUpdate {

    private Long id;

    private String username;

    private String password;

    private Long enabled;

}
