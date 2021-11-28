package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user_product_provider")
@Getter
@Setter
public class UserProductProvider {

    @Column(name = "id")
    private Long id;

    @Id
    @Column(name = "user_name")
    private String userName;

    @Column(name = "product_provider_id")
    private Long productProviderId;

}
