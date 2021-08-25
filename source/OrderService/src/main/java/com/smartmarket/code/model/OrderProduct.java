package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

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
}
