package com.smartmarket.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "clients")
public class Client implements Serializable {

	@Id
	@Column(name = "client_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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


	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getIpAccess() {
		return ipAccess;
	}

	public void setIpAccess(String ipAccess) {
		this.ipAccess = ipAccess;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getClientIdCode() {
		return clientIdCode;
	}

	public void setClientIdCode(String clientIdCode) {
		this.clientIdCode = clientIdCode;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Long getIsActive() {
		return isActive;
	}

	public void setIsActive(Long isActive) {
		this.isActive = isActive;
	}
}