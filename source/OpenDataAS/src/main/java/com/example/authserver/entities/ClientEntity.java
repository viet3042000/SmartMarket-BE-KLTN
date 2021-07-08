package com.example.authserver.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class ClientEntity {
    @Id
    private Long id;

    @Column(name = "client_id")
//    @Column(name = "clientId")
    private String clientId;

    @Column(name = "client_secret")
//    @Column(name = "clientSecret")
    private String clientSecret;	
    
    @Column(name = "authorized_grant_types")
//    @Column(name = "authorizedGrantTypes")
    private String authorizedGrantTypes;	
    
    @Column(name = "redirect_uri")
//    @Column(name = "redirectUri")
    private String redirectUri;	
    
    @Column(name = "expiration_time")
//    @Column(name = "expirationTime")
    private int expirationTime;	
}
