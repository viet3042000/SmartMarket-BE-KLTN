package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
//@Table(name = "outbox_old")
@Table(name = "outbox")
@Getter
@Setter
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobmanagement_outbox_seq")
    @SequenceGenerator(sequenceName = "jobmanagement_outbox_sequence", allocationSize = 1, name = "jobmanagement_outbox_seq")
    private Long id;

//    đích (TravelInsurance)
    @Column(name = "aggregate_type")
    private String aggregateType;

    //order_id
    @Column(name = "aggregate_id")
    private String aggregateId;

//   createOrder , cancelOrder ,...
    @Column(name = "type")
    private String type;

    @Column(name = "payload")
    private String payload;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;

}
