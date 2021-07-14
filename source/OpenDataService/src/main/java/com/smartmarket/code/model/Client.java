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
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "client_id")
	private String clientId;

	@Column(name = "secret")
	@JsonIgnore
	private String secret;

	@Column(name = "is_active")
	private Long isActive;

	@Column(name = "consumer_id")
	private String consumerId;

	@Column(name = "ip_access")
	private String ipAccess;


}