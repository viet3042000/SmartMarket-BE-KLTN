package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "saga_state")
@Getter
@Setter
public class SagaState implements Serializable {
    //= request_id
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "current_step")
    private String currentStep;

    //STARTED, SUCCEEDED, ABORTING, ABORTED, ERROR (status of the individual steps)
    @Column(name = "step_state")
    private String stepState;

    //STARTED, SUCCEEDED, ABORTING, ABORTED, ERROR (The current status of the Saga)
    @Column(name = "status")
    private String status;

    //createOrder/cancelOrder
    @Column(name = "type")
    private String type;

    @Column(name = "payload")
    private String payload;

    //@Temporal sử dụng để chú thích cho cột dữ liệu ngày tháng và thời gian (date time).
    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;

    @Column(name = "fisnished_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedLogtimestamp;

    //@Version = UPDATE MYENTITY SET ..., VERSION = VERSION + 1 WHERE ((ID = ?) AND (VERSION = ?))
    //If the WHERE clause fails to match a record
    //(because the same entity has already been updated by another thread),
    // then the persistence provider will throw an OptimisticLockException
    @Version
    @Column(name = "version")
    private int version;

    //product_id
    @Column(name = "aggregateid")
    private String aggregateId;
}
