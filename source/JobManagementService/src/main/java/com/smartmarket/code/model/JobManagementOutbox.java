package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "jobmanagement_outbox")
@Getter
@Setter
public class JobManagementOutbox {

    @Id
    private Long id;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_reference")
    private String orderReference;

    @Column(name = "start_time")
    private Long startTime;

}
