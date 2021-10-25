package com.smartmarket.code.request.entity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserUpdate {

//    private Long id;

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


}
