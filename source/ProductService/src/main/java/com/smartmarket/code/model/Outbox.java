package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "outbox")
@Getter
@Setter
public class Outbox implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(sequenceName = "outbox_sequence", allocationSize = 1, name = "order_seq")
    @Column(name = "id")
    private Long id;

    //đích
    @Column(name = "aggregatetype")
    private String aggregateType;

    //product_id
    @Column(name = "aggregateid")
    private String aggregateId;

    @Column(name = "type")
    private String type;

    @Column(name = "payload")
    private String payload;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;

}
