package com.smartmarket.code.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "client_detail")
@Getter
@Setter
public class ClientDetail implements Serializable {

	@Id
	@Column(name = "client_detail_id")
//	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_id_seq")
//	@SequenceGenerator(sequenceName = "client_id_sequence", allocationSize = 1, name = "client_id_seq")
	private Long id;

	@Column(name = "client_id")
	private String clientId;

	@Column(name = "user_name_bic")
	private String userNameBic;

	@Column(name = "password_bic")
	private String passwordBic;

	@Column(name = "domain_name_bic")
	private String domainNameBic;

	@Column(name = "ip_access")
	private String ipAccess;

	@Column(name = "is_active")
	private Long isActive;

}