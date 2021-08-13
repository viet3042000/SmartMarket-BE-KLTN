package com.smartmarket.code.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "bic_transaction")
@Getter
@Setter
public class BICTransaction implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CUST_SEQ")
	@SequenceGenerator(sequenceName = "bic_transaction_sequence", allocationSize = 1, name = "CUST_SEQ")
	private Long id;

	@Column(name = "request_id")
	private String requestId;

	@Column(name = "order_reference")
	private String orderReference;

	@Column(name = "order_id")
	private String orderId;

	@Column(name = "customer_name")
	private String customerName;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "email")
	private String email;

	@Column(name = "ord_paid_money")
	private String ordPaidMoney;

	@Column(name = "consumer_id")
	private String consumerId;

	@Column(name = "from_date")
	private String fromDate;

	@Column(name = "to_date")
	private String toDate;

	@Column(name = "log_timestamp" , columnDefinition= "TIMESTAMP WITH TIME ZONE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date logTimestamp;

	@Column(name = "result_code")
	private String resultCode;

	@Column(name = "bic_result_code")
	private String bicResultCode;

	@Column(name = "ord_date")
	private String ordDate;

	@Column(name = "product_id")
	private String productId;

	@Column(name = "customer_address")
	private String customerAddress;

	@Column(name = "client_ip")
	private String clientIp;

	@Column(name = "type")
	private String type;

    @Column(name = "destroy")
    private Long destroy;


}