package com.smartmarket.code.request.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


@Getter
@Setter
public class UserCreate {

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

}
