package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pending_bic_transaction")
@Getter
@Setter
public class PendingBICTransaction {

    @Id
    private Long id;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "order_reference")
    private String orderReference;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "count")
    private Long count;

}