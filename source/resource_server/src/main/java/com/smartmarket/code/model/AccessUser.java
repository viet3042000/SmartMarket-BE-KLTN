package com.smartmarket.code.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "access_users")
public class AccessUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_users_id", unique = true, nullable = false)
    private Long id;

    @Basic
    @Column(name = "users_urls_id")
    private Long usersUrlsId;

    @Basic
    @Column(name = "user_id")
    private Long userId;

//    @Basic
//    @Column(name = "IS_ACTIVE")
//    private Long isActive;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsersUrlsId() {
        return usersUrlsId;
    }

    public void setUsersUrlsId(Long usersUrlsId) {
        this.usersUrlsId = usersUrlsId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
