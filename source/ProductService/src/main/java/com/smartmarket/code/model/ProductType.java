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

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_type_id_seq")
    @SequenceGenerator(sequenceName = "product_type_id_sequence", allocationSize = 1, name = "product_type_id_seq")
    private Long id;

    //BIC(=productProvider(product) = username of provider)
    @Column(name = "product_type_name")
    private String productTypeName;

    @Column(name = "description")
    private String desc;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;
}
