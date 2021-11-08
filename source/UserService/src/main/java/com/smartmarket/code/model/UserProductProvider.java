package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Table(name = "user_product_provider")
@Getter
@Setter
public class UserProductProvider {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_product_provider_id_seq")
    @SequenceGenerator(sequenceName = "user_product_provider_id_sequence", allocationSize = 1, name = "user_product_provider_id_seq")
    private Long id;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "product_provider_id")
    private Long productProviderId;

}
