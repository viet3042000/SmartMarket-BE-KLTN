package com.smartmarket.code.request;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreateUserRequest implements Serializable {

    private String userName;

    private String password;

    private String email;

    private String phoneNumber;

    private String address;

    private Integer gender;

    private String identifyNumber;

    private String birthDate;

    private String fullName;

    private Integer enabled;

    private String provider;

    private String role ;
}
