package com.smartmarket.code.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "interval_history")
@Getter
@Setter
public class IntervalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "interval_history_seq")
    @SequenceGenerator(sequenceName = "interval_history_sequence", allocationSize = 1, name = "interval_history_seq")
    private Long id;

    @Column(name = "interval_id")
    private String intervalId;

    // 1,2,3,4,5
    @Column(name = "step")
    private int step;

    @Column(name = "step_detail")
    private String stepDetail;

    @Column(name = "created_at")
    private String createAt;

    @Column(name = "finished_at")
    private String finishedAt;

    //running/succeeded/failed
    @Column(name = "state")
    private String state;
}
