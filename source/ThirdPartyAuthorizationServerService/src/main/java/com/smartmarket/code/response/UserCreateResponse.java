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

    private Long gender;

    private String identifyNumber;

    private String birthDate;

    private String fullName;

    private Long enabled;

    private String role ;



}
