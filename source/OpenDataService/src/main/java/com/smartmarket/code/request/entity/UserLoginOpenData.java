package com.smartmarket.code.request.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class UserLoginOpenData implements Serializable {

    private String username ;
    private String password ;

}
