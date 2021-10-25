package com.smartmarket.code.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


@Getter
@Setter
public class UserCreateResponse {

    private Long id;

    private String userName;

    private String password;

    private String email;

    private String phoneNumber;

    private String address;

    private Integer gender;

    private String identifyNumber;

    private String birthDate;

    private String fullName;

    private int enabled;

    private String role ;

}
