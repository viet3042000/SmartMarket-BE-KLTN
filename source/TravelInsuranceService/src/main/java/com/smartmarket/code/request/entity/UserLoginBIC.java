package com.smartmarket.code.request.entity;

import java.io.Serializable;

public class UserLoginBIC  implements Serializable {

    private String username ;
    private String password ;
    private String domainname ;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDomainname() {
        return domainname;
    }

    public void setDomainname(String domainname) {
        this.domainname = domainname;
    }
}
