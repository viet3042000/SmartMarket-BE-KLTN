package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "access_token")
@Getter
@Setter
public class AccessToken implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_token_id", unique = true, nullable = false)
    private Long id;

    @Basic
    @Column(name = "user_name")
    private String userName;

    @Basic
    @Column(name = "pass_word")
    private String passWord;

    @Basic
    @Column(name = "domain_name")
    private String domainName;

    @Basic
    @Column(name = "token")
    private String token;

    @Basic
    @Column(name = "issue_time")
    private Long issueTime;

    @Basic
    @Column(name = "expire_time")
    private Long expireTime;


}
