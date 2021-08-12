package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "jobmanagement_outbox")
@Getter
@Setter
public class JobManagementOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "jobmanagement_outbox_seq")
    @SequenceGenerator(sequenceName = "jobmanagement_outbox_sequence", allocationSize = 1, name = "jobmanagement_outbox_seq")
    private Long id;

    @Column(name = "request_id")
    private String requestId;

}
