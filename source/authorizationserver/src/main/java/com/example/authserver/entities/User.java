package com.example.authserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements Serializable{
//    @Id
//    private Long id;
//
//    @Column(name = "user_name")
////    @Column(name = "userName")
//    private String userName;
//
//    @Column(name = "user_password")
////    @Column(name = "userPassword")
//    private String password;
//
//    @Column(name = "enabled")
//    private String enabled;


    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_password")
    private String password;

    @Column(name = "enabled")
    private Integer enabled;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @Column(name = "email")
    private String email;
}
