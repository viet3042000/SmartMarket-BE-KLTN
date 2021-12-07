package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "product_approval_flow")
@Getter
@Setter
public class ProductApprovalFlow implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_approval_flow_seq")
    @SequenceGenerator(sequenceName = "product_approval_flow_sequence", allocationSize = 1, name = "product_approval_flow_seq")
    @Column(name = "id")
    private Long id;

    //createProduct/updateProduct/deleteProduct/...
    @Column(name = "flow_name")
    private String flowName;

    @Column(name = "product_id")
    private Long productId;

    //null(default)
    @Column(name = "step_detail")
    private String stepDetail;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;
}
