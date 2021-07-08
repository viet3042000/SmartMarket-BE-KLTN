package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users_urls")
@Getter
@Setter
public class UserUrl implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_urls_id", unique = true, nullable = false)
    private Long id;

    @Basic
    @Column(name = "authorities")
    private String authorities;

    @Basic
    @Column(name = "user_name")
    private String userName;

    @Basic
    @Column(name = "url_name")
    private String urlName;

}
