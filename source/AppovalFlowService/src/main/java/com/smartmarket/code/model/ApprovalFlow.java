package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "approval_flow")
@Getter
@Setter
public class ApprovalFlow implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "approval_flow_seq")
    @SequenceGenerator(sequenceName = "approval_flow_sequence", allocationSize = 1, name = "approval_flow_seq")
    @Column(name = "id")
    private Long id;

    //createProduct/updateProduct/deleteProduct/...
    @Column(name = "flow_name")
    private String flowName;

    //BICTRV
    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_provider_id")
    private Long productProviderId;

    //null(default)
    @Column(name = "step_detail")
    private String stepDetail;

    @Column(name = "number_of_steps")
    private int numberOfSteps;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;
}
