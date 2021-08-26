package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "order_product")
@Getter
@Setter
public class OrderProduct implements Serializable {

    @Id
    @Column(name = "order_id")
    private String orderId;

    //request body
    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    //Succeeded/Aborted
    @Column(name = "state")
    private String state;

    @Column(name = "fisnished_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedLogtimestamp;
}
