package com.smartmarket.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "travel_insurance")
@Getter
@Setter
public class TravelInsurance implements Serializable {

    //=order_ref
    @Id
    @Column(name = "id")
    private String id;

    //creating/updating/succeeded
    @Column(name = "state")
    @JsonIgnore
    private String state;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "created_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdLogtimestamp;

    @Column(name = "fisnished_logtimestamp", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishedLogtimestamp;
}
