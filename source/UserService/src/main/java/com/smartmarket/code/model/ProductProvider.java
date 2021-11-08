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
    private Long id;

    //BIC
    @Column(name = "product_provider_name")
    private String productProviderName;

    @Column(name = "description")
    private String desc;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;
}
