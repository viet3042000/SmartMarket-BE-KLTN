package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "job")
@Getter
@Setter
//save information of each job
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_seq")
    @SequenceGenerator(sequenceName = "job_sequence", allocationSize = 1, name = "job_seq")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "interval_configuration")
    private Long intervalConfig;

    //this job is enable to run manage()
    @Column(name = "enable")
    private String enable;

    @Column(name = "max_amount_step")
    private int maxAmountStep;
}