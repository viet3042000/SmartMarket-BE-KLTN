package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "service_config")
@Getter
@Setter
public class ServiceConfig implements Serializable {


//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
//	@SequenceGenerator(sequenceName = "bic_transaction_sequence", allocationSize = 1, name = "CUST_SEQ")
	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "key")
	private String requestId;

	@Column(name = "value")
	private String orderReference;

	@Column(name = "description")
	private String orderId;

	@Column(name = "is_active")
	private String customerName;


}