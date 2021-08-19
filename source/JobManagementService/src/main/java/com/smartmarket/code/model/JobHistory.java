package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

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

    //running/succeeded/error/failed
    @Column(name = "state")
    private String state;

    @Column(name = "amount_step")
    private int amountStep;

    // 1/5 or 2/5
    @Column(name = "current_step")
    private String currentStep;

    @Column(name = "created_at")
    private String createAt;

    @Column(name = "finished_at")
    private String finishedAt;

}
