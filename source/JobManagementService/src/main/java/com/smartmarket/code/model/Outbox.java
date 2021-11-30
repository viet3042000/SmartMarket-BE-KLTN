package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "outbox")
@Getter
@Setter
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobmanagement_outbox_seq")
    @SequenceGenerator(sequenceName = "jobmanagement_outbox_sequence", allocationSize = 1, name = "jobmanagement_outbox_seq")
    private Long id;

    //đích (travelinsurance)
//    @Column(name = "aggregatetype")
//    private String aggregateType;
//
//    //order_id
//    @Column(name = "aggregateid")
//    private String aggregateId;
////   createOrder , cancelOrder ,...
//    @Column(name = "type")
//    private String type;
//
//    @Column(name = "payload")
//    private String payload;


//    pending_bic_transaction id
    @Column(name = "pending_id")
    private Long pendingId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_reference")
    private String orderReference;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "interval_id")
    private String intervalId;

    // 1,2,3,4,5
    @Column(name = "step")
    private int step;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;

}
