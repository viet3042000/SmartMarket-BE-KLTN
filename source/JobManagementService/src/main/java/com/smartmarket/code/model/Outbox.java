package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "outbox")
@Getter
@Setter
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobmanagement_outbox_seq")
    @SequenceGenerator(sequenceName = "jobmanagement_outbox_sequence", allocationSize = 1, name = "jobmanagement_outbox_seq")
    private Long id;

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

}
