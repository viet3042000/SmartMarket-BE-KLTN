package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "saga_state")
@Getter
@Setter
public class SagaState {
    //= request_id
    @Id
    @Column(name = "id")
    private String id;

//    @Column(name = "order_id")
//    private UUID orderId;

    @Column(name = "current_step")
    private String currentStep;

    //STARTED, SUCCEEDED, ABORTING, or ABORTED
    @Column(name = "step_state")
    private String stepState;

    //STARTED, SUCCEEDED, ABORTING, or ABORTED
    @Column(name = "status")
    private String status;

    //order-placement/createTravelInsuranceBIC/...
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

    @Version
    @Column(name = "version")
    private int version;
}