package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrdersServiceEntity implements Serializable {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
//	@SequenceGenerator(sequenceName = "order_sequence", allocationSize = 1, name = "order_seq")
//    @Column(name = "id")
//    private Long id;

//    @Column(name = "order_id")
//    private UUID orderId;

    @Id
    @Column(name = "order_id")
    private String orderId;

    //request body
    @Column(name = "payload")
    private String payload;

    @Column(name = "payload_update")
    private String payloadUpdate;

    @Column(name = "payload_get")
    private String payloadGet;

    @Column(name = "type")
    private String type;

    //Pending/Success/Aborted
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

}
