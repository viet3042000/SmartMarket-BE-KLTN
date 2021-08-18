package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "job_history")
@Getter
@Setter
public class JobHistory {

    @Column(name = "name")
    private String name;

    @Id
    @Column(name = "interval_id")
    private String intervalId;

    //state of 1 interval id (success if all pending_order is success, else is failure)
    @Column(name = "state")
    private String state;

    //request id,order id,order ref of each pending_order in interval
    @Column(name = "interval_detail")
    private String intervalDetail;
}
