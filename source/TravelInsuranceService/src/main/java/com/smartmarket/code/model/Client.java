package com.smartmarket.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client implements Serializable {

	@Id
	@Column(name = "client_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_seq")
	@SequenceGenerator(sequenceName = "client_id_sequence", allocationSize = 1, name = "client_id_seq")
	private Long id;

	@Column(name = "client_id_code")
	private String clientIdCode;

	@Column(name = "secret")
	@JsonIgnore
	private String secret;

	@Column(name = "is_active")
	private Long isActive;

	@Column(name = "consumer_id")
	private String consumerId;

	@Column(name = "ip_access")
	private String ipAccess;

	@Column(name = "client_id_sync")
	private Long clientIdSync;
}