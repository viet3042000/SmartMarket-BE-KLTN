package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "product_type")
@Getter
@Setter
public class ProductType implements Serializable {

//    @Id
    @Column(name = "id")
    private Long id;

    //BIC(=productProvider(product) = username of provider)
    @Id
    @Column(name = "product_type_name")
    private String productTypeName;

    @Column(name = "description")
    private String desc;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;
}
