package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter
@Setter
public class Outbox {
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

    @Column(name = "payload")
    private String payload;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;
}
