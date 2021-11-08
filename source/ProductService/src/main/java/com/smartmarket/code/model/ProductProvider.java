package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "product_provider")
@Getter
@Setter
public class ProductProvider implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_provider_id_seq")
    @SequenceGenerator(sequenceName = "product_provider_id_sequence", allocationSize = 1, name = "product_provider_id_seq")
    private Long id;

    //BIC(=productProvider(product) = username of provider)
    @Column(name = "product_provider_name")
    private String productProviderName;

    @Column(name = "description")
    private String desc;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;
}
