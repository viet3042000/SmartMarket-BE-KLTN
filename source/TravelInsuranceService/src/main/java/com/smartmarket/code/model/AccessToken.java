package com.smartmarket.code.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "access_token")
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



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Long issueTime) {
        this.issueTime = issueTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }
}
