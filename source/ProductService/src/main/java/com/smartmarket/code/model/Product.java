package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_seq")
    @SequenceGenerator(sequenceName = "product_id_sequence", allocationSize = 1, name = "product_id_seq")
    private Long id;

    //BICTRV
    @Column(name = "product_name")
    private String productName;

    //bao hiem du lich
    @Column(name = "type")
    private String type;

    //BIC (=username of provider)
    @Column(name = "product_provider")
    private String productProvider;

    @Column(name = "description")
    private String desc;

    //ct tinh
    @Column(name = "price")
    private String price;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;

    ////Succeeded, Pending, .., (almost similar order state)
    @Column(name = "state")
    private String state;

    @Column(name = "current_saga_id")
    private String currentSagaId;

//    @Column(name = "fisnished_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date finishedLogtimestamp;
}
