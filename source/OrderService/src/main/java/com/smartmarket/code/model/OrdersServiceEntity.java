package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class OrdersServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
	@SequenceGenerator(sequenceName = "order_sequence", allocationSize = 1, name = "order_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "payload")
    private String payload;

    @Column(name = "type")
    private String type;

    @Column(name = "state")
    private String state;
}
