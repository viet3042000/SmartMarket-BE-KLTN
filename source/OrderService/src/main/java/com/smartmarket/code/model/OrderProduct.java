package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "order_product")
@Getter
@Setter
public class OrderProduct implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_product_seq")
    @SequenceGenerator(sequenceName = "order_product_sequence", allocationSize = 1, name = "order_product_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id")
    private String orderId;

    //orderReference
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "index")
    private Integer index;

    //Succeeded/Canceling/Canceled/Aborted/Aborting/Error
    @Column(name = "state")
    private String state;

    @Column(name = "fisnished_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedLogtimestamp;

    //price
    @Column(name = "item_price")
    private String itemPrice;
}
