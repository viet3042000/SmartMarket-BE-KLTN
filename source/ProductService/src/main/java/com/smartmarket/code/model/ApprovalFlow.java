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
    @Column(name = "id")
    private Long id;

    //createProduct/updateProduct/deleteProduct/...
    @Column(name = "flow_name")
    private String flowName;

    @Column(name = "product_provider_name")
    private String productProviderName;

    //null(default)
    @Column(name = "step_detail")
    private String stepDetail;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;
}
