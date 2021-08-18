package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "travelinsurance_outbox")
@Getter
@Setter
public class TravelinsuranceOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "travelinsurance_outbox_seq")
    @SequenceGenerator(sequenceName = "travelinsurance_outbox_sequence", allocationSize = 1, name = "travelinsurance_outbox_seq")
    @Column(name = "id")
    private Long id;

    //service đích
    @Column(name = "aggregatetype")
    private String aggregateType;

    @Column(name = "aggregateid")
    private String aggregateId;

    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "payload")
    private String payload;

//    @Column(name = "order_id")
//    private UUID orderId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "interval_id")
    private String intervalId;

    @Column(name = "order_reference")
    private String orderReference;
}
