package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_outbox")
@Getter
@Setter
public class OrderOutbox {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outbox_seq")
    @SequenceGenerator(sequenceName = "outbox_sequence", allocationSize = 1, name = "outbox_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "aggregatetype")
    private String aggregateType;

    @Column(name = "aggregateid")
    private String aggregateId;

    @Column(name = "type")
    private String type;

    @Column(name = "payload")
    private String payload;
}
