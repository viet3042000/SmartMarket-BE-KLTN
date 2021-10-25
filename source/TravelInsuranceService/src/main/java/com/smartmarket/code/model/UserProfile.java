package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "user_profile")
@Getter
@Setter
public class UserProfile implements Serializable {

//    @Id
    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "user_name")
    private String userName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "identify_number")
    private String identifyNumber;

    @Column(name = "birth_date")
    private String birthDate;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "create_date" ,  columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "enabled")
    private Integer enabled;

}