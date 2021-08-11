package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "order_outbox")
@Getter
@Setter
public class OrderOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(sequenceName = "outbox_sequence", allocationSize = 1, name = "order_seq")
    @Column(name = "id")
    private Long id;

    //service đích
    @Column(name = "aggregatetype")
    private String aggregateType;

    @Column(name = "aggregateid")
    private String aggregateId;

    @Column(name = "type")
    private String type;

    @Column(name = "payload")
    private String payload;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "host_name")
    private String hostName;
}
