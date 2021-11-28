package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Orders implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
//	@SequenceGenerator(sequenceName = "order_sequence", allocationSize = 1, name = "order_seq")
//    @Column(name = "id")
//    private Long id;

    @Id
    @Column(name = "order_id")
    private String orderId;

    //request body
    @Column(name = "payload")
    private String payload;

    //Pending/Succeeded/Aborted/Paid
    @Column(name = "state")
    private String state;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;

    @Column(name = "fisnished_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedLogtimestamp;

    @Column(name = "user_name")
    private String userName;

    //number_of_items
    @Column(name = "quantity_items")
    private Integer quantityItems;

    //price
    @Column(name = "order_price")
    private String orderPrice;

}
