package com.example.authserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity {
    @Id
    private Long id;

    @Column(name = "user_name")
//    @Column(name = "userName")
    private String userName;

    @Column(name = "user_password")
//    @Column(name = "userPassword")
    private String userPassword;	
    
    @Column(name = "enabled")
    private String enabled;	
}
