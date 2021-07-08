package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "clients_urls")
@Getter
@Setter
public class ClientUrl implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clients_urls_id", unique = true, nullable = false)
    private Long id;

//    @Column(name = "client_id")
//    private Long clientId;
//
//    @Column(name = "url_id")
//    private Long urlId;

    @Column(name = "IS_ACTIVE")
    private Long isActive;

    @Column(name = "client_id_name")
    private String clientIdName;

    @Column(name = "url_name")
    private String urlName;

}
