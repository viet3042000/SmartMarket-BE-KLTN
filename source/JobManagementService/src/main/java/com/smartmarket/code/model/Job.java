package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "job")
@Getter
@Setter
public class Job {
    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "interval_configuration")
    private Long intervalConfig;

    //this job is enable to run manage()
    @Column(name = "enable")
    private String enable;

    @Column(name = "amount_step")
    private int amountStep;
}