package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrdersServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
	@SequenceGenerator(sequenceName = "order_sequence", allocationSize = 1, name = "order_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "payload")
    private String payload;

    @Column(name = "payload_update")
    private String payloadUpdate;

    @Column(name = "payload_get")
    private String payloadGet;

    @Column(name = "type")
    private String type;

    @Column(name = "state")
    private String state;


    @Column(name = "created_at", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "created_finish", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createFinish;

    @Column(name = "user_name")
    private String userName;

}
