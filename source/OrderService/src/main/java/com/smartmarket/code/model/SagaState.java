package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "saga_state")
@Getter
@Setter
public class SagaState {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sagastate_seq")
    @SequenceGenerator(sequenceName = "sagastate_sequence", allocationSize = 1, name = "sagastate_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "current_step")
    private String currentStep;

    @Column(name = "step_state")
    private String stepState;

    //STARTED, SUCCEEDED, ABORTING, or ABORTED
    @Column(name = "status")
    private String status;

    //create/update/rollback
    @Column(name = "type")
    private String type;

    @Column(name = "payload")
    private String payload;

    //version
}