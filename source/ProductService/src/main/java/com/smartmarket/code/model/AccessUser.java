package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "access_users")
@Getter
@Setter
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

}
