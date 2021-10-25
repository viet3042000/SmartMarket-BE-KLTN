package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pending_bic_transaction")
@Getter
@Setter
public class PendingBICTransaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pending_bic_transaction_seq")
    @SequenceGenerator(sequenceName = "pending_bic_transaction_sequence", allocationSize = 1, name = "pending_bic_transaction_seq")
    private Long id;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "order_reference")
    private String orderReference;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "count")
    private Long count;

    @Column(name = "type")
    private String type;
}
