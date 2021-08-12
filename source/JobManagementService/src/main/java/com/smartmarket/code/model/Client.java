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

	@Column(name = "id")
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_seq")
//	@SequenceGenerator(sequenceName = "client_id_sequence", allocationSize = 1, name = "client_id_seq")
	private Long id;

	@Id
	@Column(name = "client_id")
	private String clientId;

	@Column(name = "client_secret")
	@JsonIgnore
	private String secret;

	@Column(name = "consumer_id")
	private String consumerId;

//	@Column(name = "ip_access")
//	private String ipAccess;

}